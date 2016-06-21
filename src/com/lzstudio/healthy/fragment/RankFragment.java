package com.lzstudio.healthy.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.lzstudio.healthy.R;
import com.lzstudio.healthy.adapter.RankAdapter;
import com.lzstudio.healthy.bean.User;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;
import com.lzstudio.healthy.view.titanic.Titanic;
import com.lzstudio.healthy.view.titanic.TitanicTextView;
import com.lzstudio.healthy.view.titanic.Typefaces;
import com.umeng.analytics.MobclickAgent;

public class RankFragment extends Fragment implements ResponseData {
	private View view;
	private PullToRefreshListView rank_listview;
	private LinearLayout rank_loading;
	private Titanic titanic;
	private TitanicTextView tv_loading;
	private int page = 0;
	private int pageCount = 20;
	private List<User> list = new ArrayList<User>();
	private List<User> users;
	private RankAdapter adapter;
	private boolean isRefresh = false;
	private boolean isUpRefresh = false;
	private static final int NO_NETWORK = 11;
	private static final int IS_REFRESH = 12;
	private static final int IS_UP_REFRESH = 13;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case NO_NETWORK:
				CommonUtil.showToast(getActivity(), "请检查您的网络...");
				break;
			case IS_REFRESH:
				isRefresh = false;
				rank_listview.onRefreshComplete();
				Toast.makeText(getActivity(), "貌似没有网络了哦！", Toast.LENGTH_SHORT)
						.show();
				break;
			case IS_UP_REFRESH:
				isUpRefresh = false;
				rank_listview.onRefreshComplete();
				Toast.makeText(getActivity(), "貌似没有网络了哦！请稍后再试...",
						Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		list.clear();
		view = inflater.inflate(R.layout.rank_fragment, container, false);
		initView();
		rank_listview.setOnRefreshListener(new OnRefreshListener2<ListView>() {
			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isUpRefresh) {
					page = 0;
					setLastUpdateTime();
					isRefresh = true;
					loadDatas();
				}
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				if (!isRefresh) {
					page++;
					isUpRefresh = true;
					loadDatas();
				}
			}
		});
		loadDatas();
		setLastUpdateTime();
		return view;
	}

	private void initView() {
		rank_listview = (PullToRefreshListView) view
				.findViewById(R.id.rank_lv_pulltorefresh);
		rank_loading = (LinearLayout) view.findViewById(R.id.rank_loading);
		tv_loading = (TitanicTextView) view.findViewById(R.id.my_text_view);
		tv_loading.setTypeface(Typefaces.get(getActivity(),
				"Satisfy-Regular.ttf"));
		titanic = new Titanic();
		titanic.start(tv_loading);
	}

	/**
	 * 联网加载数据
	 */
	private void loadDatas() {
		if (CommonUtil.isNetworkAvailable(getActivity()) != 0) {
			HttpClientUtils.getInstance().rankJson(this, Url.Rank_Host, page,
					pageCount);
		} else {
			if (!isRefresh && !isUpRefresh) {
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

	@Override
	public void getResponseData(int id, String result) {
		if (id == 1) {
			dealRusult(result);
		} else {
			Toast.makeText(getActivity(), "获取数据出错", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 处理json数据
	 * 
	 * @param result
	 */
	private void dealRusult(String result) {
		users = NewsParser.getInstance().jsonToUsersModle(result);
		if (users != null) {
			if (list.size() == 0) {
				list.addAll(users);
				adapter = new RankAdapter(getActivity(), list);
				rank_listview.getRefreshableView().setAdapter(adapter);
				if (list.size() < 20) {
					rank_listview.onRefreshComplete();
					Toast.makeText(getActivity(), "没有更多数据了...",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				if (isRefresh) {
					isRefresh = false;
					list.removeAll(list);
					list.addAll(users);
					rank_listview.onRefreshComplete();
				} else if (isUpRefresh) {
					isUpRefresh = false;
					list.addAll(users);
					rank_listview.onRefreshComplete();
				}
			}
			adapter.notifyDataSetChanged();
		} else {
			Toast.makeText(getActivity(), "网络不佳,请稍候重试!", Toast.LENGTH_SHORT)
					.show();
		}
		if (rank_loading.isShown()) {
			titanic.cancel();
			rank_loading.setVisibility(View.GONE);
		}
	}

	private void setLastUpdateTime() {
		String text = CommonUtil.getStringDate();
		rank_listview.getLoadingLayoutProxy().setLastUpdatedLabel(text);
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
