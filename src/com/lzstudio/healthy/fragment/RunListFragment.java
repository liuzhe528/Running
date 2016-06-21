package com.lzstudio.healthy.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.RunDetailActivity;
import com.lzstudio.healthy.adapter.RunAdapter;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshBase;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.lzstudio.healthy.view.pulltorefresh.PullToRefreshListView;
import com.lzstudio.healthy.view.titanic.Titanic;
import com.lzstudio.healthy.view.titanic.TitanicTextView;
import com.lzstudio.healthy.view.titanic.Typefaces;
import com.umeng.analytics.MobclickAgent;

public class RunListFragment extends Fragment implements ResponseData {
	private SharedPreferences sp;
	private PullToRefreshListView pullToRefreshListView;
	private LinearLayout ll_loading;
	private View view;
	private Titanic titanic;
	private TitanicTextView tv_loading;
	private List<Run> runList;
	private List<Run> list = new ArrayList<Run>();
	private RelativeLayout rl_runlist_noting;
	private RunAdapter adapter;
	private boolean isRefresh = false;
	private static final int NO_NETWORK = 101;
	private static final int IS_REFRESH = 102;
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
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list.clear();
		view = inflater.inflate(R.layout.list_run_info, container, false);
		initView();
		sp = getMyActivity()
				.getSharedPreferences("login", Context.MODE_PRIVATE);
		pullToRefreshListView.setScrollLoadEnabled(true);
		pullToRefreshListView.setPullLoadEnabled(false);
		pullToRefreshListView.onPullUpRefreshComplete();
		pullToRefreshListView.setHasMoreData(false);
		pullToRefreshListView.setPullRefreshEnabled(true);
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
									RunDetailActivity.class);
							intent.putExtra("runinfo", list.get(position));
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
						isRefresh = true;
						pullToRefreshListView.setPullRefreshEnabled(false);
						loadDatas();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
					}
				});
		ll_loading.setVisibility(View.VISIBLE);
		titanic.start(tv_loading);
		loadDatas();
		setLastUpdateTime();
		return view;
	}

	/**
	 * 联网加载数据
	 */
	private void loadDatas() {
		if (CommonUtil.isNetworkAvailable(getActivity()) != 0) {
			HttpClientUtils.getInstance().getRunListInfo(this, Url.ONGOING,
					sp.getString("phoneNumber", ""));
		} else {
			if (!isRefresh) {
				handler.sendEmptyMessage(NO_NETWORK);
			} else {
				handler.sendEmptyMessage(IS_REFRESH);
			}
		}
	}

	private void initView() {
		pullToRefreshListView = (PullToRefreshListView) view
				.findViewById(R.id.lv_run_pulltorefresh);
		ll_loading = (LinearLayout) view.findViewById(R.id.run_loading);
		tv_loading = (TitanicTextView) view.findViewById(R.id.my_text_view);
		tv_loading.setTypeface(Typefaces.get(getMyActivity(),
				"Satisfy-Regular.ttf"));
		titanic = new Titanic();
		rl_runlist_noting = (RelativeLayout) view
				.findViewById(R.id.rl_runlist_nothing);
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == 1) {
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
		runList = NewsParser.getInstance().ongoingJsonToBean(result);
		if (runList != null && runList.size() != 0) {
			rl_runlist_noting.setVisibility(View.GONE);
			if (list.size() == 0) {
				list.addAll(runList);
				adapter = new RunAdapter(getMyActivity(), list);
				pullToRefreshListView.getRefreshableView().setAdapter(adapter);
				pullToRefreshListView.onPullDownRefreshComplete();
			} else {
				if (isRefresh) {
					isRefresh = false;
					list.removeAll(list);
					list.addAll(runList);
					pullToRefreshListView.onPullDownRefreshComplete();
					pullToRefreshListView.setPullRefreshEnabled(true);
				}
			}
			adapter.notifyDataSetChanged();
		} else {
			rl_runlist_noting.setVisibility(View.VISIBLE);
		}
		if (ll_loading.isShown()) {
			titanic.cancel();
			ll_loading.setVisibility(View.GONE);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			ll_loading.setVisibility(View.VISIBLE);
			titanic.start(tv_loading);
			loadDatas();
			setLastUpdateTime();
		}
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
	public void onStop() {
		rl_runlist_noting.setVisibility(View.GONE);
		super.onStop();
	}

	@Override
	public void onResume() {
		ll_loading.setVisibility(View.VISIBLE);
		titanic.start(tv_loading);
		loadDatas();
		setLastUpdateTime();
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}
}
