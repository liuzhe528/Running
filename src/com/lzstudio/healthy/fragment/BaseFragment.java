package com.lzstudio.healthy.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.NewsDetailActivity;
import com.lzstudio.healthy.adapter.NewsAdapter;
import com.lzstudio.healthy.bean.NewsModle;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;
import com.lzstudio.healthy.utils.ACache;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshBase;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshListView;
import com.lzstudio.healthy.view.titanic.Titanic;
import com.lzstudio.healthy.view.titanic.TitanicTextView;
import com.lzstudio.healthy.view.titanic.Typefaces;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment implements ResponseData {
	private PullToRefreshListView pullToRefreshListView;
	private LinearLayout ll_loading;
	private View view;
	private Titanic titanic;
	private TitanicTextView tv_loading;
	private String cacheName;
	private List<NewsModle> newsList;
	private List<NewsModle> list = new ArrayList<NewsModle>();
	private int index = 1;
	private NewsAdapter adapter;
	private boolean isRefresh = false;
	private boolean isUpRefresh = false;
	private static final int NO_NETWORK = 101;
	private static final int IS_REFRESH = 102;
	private static final int IS_UP_REFRESH = 103;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NO_NETWORK:
				CommonUtil.showToast(getMyActivity(), "请检查您的网络...");
				break;
			case IS_REFRESH:
				isRefresh = false;
				pullToRefreshListView.onPullDownRefreshComplete();
				pullToRefreshListView.setPullRefreshEnabled(true);
				Toast.makeText(getMyActivity(), "貌似没有网络了哦！", Toast.LENGTH_SHORT)
						.show();
				break;
			case IS_UP_REFRESH:
				isUpRefresh = false;
				pullToRefreshListView.onPullUpRefreshComplete();
				pullToRefreshListView.setPullLoadEnabled(true);
				Toast.makeText(getMyActivity(), "貌似没有网络了哦！请稍后再试...",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	public BaseFragment(String id) {
		cacheName = id;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list.clear();
		view = inflater.inflate(R.layout.news_list, container, false);
		initView();
		pullToRefreshListView.setScrollLoadEnabled(true);
		pullToRefreshListView.setPullLoadEnabled(true);
		pullToRefreshListView.setPullRefreshEnabled(true);
		pullToRefreshListView.setHasMoreData(false);
		pullToRefreshListView.getRefreshableView().setOnItemClickListener(
				new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						if (CommonUtil.isNetworkAvailable(getActivity()) != 0) {
							list.get(position).setHasRead(true);
							View cell = pullToRefreshListView
									.getRefreshableView()
									.getChildAt(
											position
													- pullToRefreshListView
															.getRefreshableView()
															.getFirstVisiblePosition());
							if (cell != null) {
								adapter.getView(position, cell,
										pullToRefreshListView
												.getRefreshableView());
							}
							Intent intent = new Intent(getMyActivity(),
									NewsDetailActivity.class);
							intent.putExtra("newsId", list.get(position)
									.getId());
							startActivity(intent);
						} else {
							handler.sendEmptyMessage(NO_NETWORK);
						}
					}
				});
		pullToRefreshListView
				.setOnRefreshListener(new OnRefreshListener<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						setLastUpdateTime();
						index = 1;
						isRefresh = true;
						pullToRefreshListView.setPullRefreshEnabled(false);
						loadDatas();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						index++;
						isUpRefresh = true;
						loadDatas();
					}
				});
		loadDatas();
		setLastUpdateTime();
		return view;
	}

	/**
	 * 联网加载数据
	 */
	private void loadDatas() {
		if (CommonUtil.isNetworkAvailable(getActivity()) != 0) {
			HttpClientUtils.getInstance().getJsonDatas(this, index + "",
					cacheName);
		} else {
			if (!isRefresh && !isUpRefresh) {
				String json = getCacheStr(cacheName);
				if (!TextUtils.isEmpty(json)) {
					dealRusult(json);
				}
				handler.sendEmptyMessage(NO_NETWORK);
			} else {
				if (isRefresh) {
					handler.sendEmptyMessage(IS_REFRESH);
				} else if (isUpRefresh) {
					handler.sendEmptyMessage(IS_UP_REFRESH);
				}
			}
		}
	}

	private void initView() {
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.lv_pulltorefresh);
		ll_loading = (LinearLayout) view.findViewById(R.id.healthy_loading);
		tv_loading = (TitanicTextView) view.findViewById(R.id.my_text_view);
		tv_loading.setTypeface(Typefaces.get(getMyActivity(),
				"Satisfy-Regular.ttf"));
		titanic = new Titanic();
		titanic.start(tv_loading);
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == 0) {
			if (index == 1) {
				// 设置缓存
				setCacheStr(cacheName, result);
			}
			dealRusult(result);
		} else {
			Toast.makeText(getMyActivity(), "获取数据出错", Toast.LENGTH_SHORT)
					.show();
		}
	}

	/**
	 * 处理json数据
	 * 
	 * @param result
	 */
	private void dealRusult(String result) {
		newsList = NewsParser.getInstance().jsonToNewsModle(result);
		if (newsList != null) {
			if (list.size() == 0) {
				list.addAll(newsList);
				adapter = new NewsAdapter(getMyActivity(), list);
				pullToRefreshListView.getRefreshableView().setAdapter(adapter);
			} else {
				if (isRefresh) {
					isRefresh = false;
					list.removeAll(list);
					list.addAll(newsList);
					pullToRefreshListView.onPullDownRefreshComplete();
					pullToRefreshListView.setPullRefreshEnabled(true);
				} else if (isUpRefresh) {
					isUpRefresh = false;
					list.addAll(newsList);
					pullToRefreshListView.onPullUpRefreshComplete();
					pullToRefreshListView.setPullLoadEnabled(true);
				}
			}
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getMyActivity(), "网络不佳,请稍候重试!", Toast.LENGTH_SHORT)
					.show();
		}
		if (ll_loading.isShown()) {
			titanic.cancel();
			ll_loading.setVisibility(View.GONE);
		}
	}

	/**
	 * 设置缓存数据（key,value）
	 */
	public void setCacheStr(String key, String value) {
		if (!TextUtils.isEmpty(value)) {
			ACache.get(getMyActivity()).put(key, value);
		}
	}

	/**
	 * 获取缓存数据根据key
	 */
	public String getCacheStr(String key) {
		return ACache.get(getMyActivity()).getAsString(key);
	}

	private Context getMyActivity() {
		return ((Context) getActivity());
	}

	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		pullToRefreshListView.setLastUpdatedLabel(text);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}
}
