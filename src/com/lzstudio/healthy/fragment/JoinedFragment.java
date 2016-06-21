package com.lzstudio.healthy.fragment;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.adapter.RunnerAdapter;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.bean.User;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;
import com.umeng.analytics.MobclickAgent;

public class JoinedFragment extends Fragment implements ResponseData {
	private View view;
	private TextView tv_loading, tv_joined_title, tv_joined_yuedingtime,
			tv_joined_address, tv_joined_describe, tv_joined_fabutime,
			tv_joined_owner;
	private ListView listview;
	private RelativeLayout rl_joined_loading;
	private RelativeLayout rl_joined_nothing;
	private SharedPreferences sp;
	private List<Object> list;
	private List<User> userlist;
	private Run run;
	private RunnerAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.joined_run_info, container, false);
		sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		initView();
		initDatas();
		return view;
	}

	private void initDatas() {
		if (CommonUtil.isNetworkAvailable(getActivity()) != 0) {
			rl_joined_loading.setVisibility(View.VISIBLE);
			HttpClientUtils.getInstance().joined(this, Url.JOINED_RUN,
					sp.getString("phoneNumber", ""));
		} else {
			Toast.makeText(getActivity(), "请检查您的网络...", Toast.LENGTH_LONG)
					.show();
		}
	}

	private void initView() {
		tv_joined_title = (TextView) view.findViewById(R.id.tv_joined_title);
		tv_joined_fabutime = (TextView) view
				.findViewById(R.id.tv_joined_fabutime);
		tv_joined_address = (TextView) view
				.findViewById(R.id.tv_joined_address);
		tv_joined_yuedingtime = (TextView) view
				.findViewById(R.id.tv_joined_yuedingtime);
		tv_joined_describe = (TextView) view
				.findViewById(R.id.tv_joined_describe);
		// 使得textview具有滑动的功能
		tv_joined_describe.setMovementMethod(ScrollingMovementMethod
				.getInstance());
		tv_joined_owner = (TextView) view.findViewById(R.id.tv_joined_owner);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
		tv_loading.setText("正在查询...");
		rl_joined_loading = (RelativeLayout) view
				.findViewById(R.id.rl_joined_loading);
		rl_joined_nothing = (RelativeLayout) view
				.findViewById(R.id.rl_joined_nothing);
		listview = (ListView) view.findViewById(R.id.lv_runner);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			initDatas();
		}
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

	@Override
	public void getResponseData(int id, String result) {
		if (id == 1) {
			list = NewsParser.getInstance().joinedJsonToBean(result);
			if (list.size() == 0) {// 没有参加
				rl_joined_nothing.setVisibility(View.VISIBLE);
			} else {
				rl_joined_loading.setVisibility(View.GONE);
				rl_joined_nothing.setVisibility(View.GONE);
				run = (Run) list.get(0);
				userlist = (List<User>) list.get(1);
				tv_joined_title.setText(run.getTitle());
				tv_joined_yuedingtime.setText("约定时间：" + run.getYuedingtime());
				tv_joined_address.setText("约定地点：" + run.getAddress());
				tv_joined_describe.setText(run.getDescribe());
				tv_joined_owner.setText("发布者：" + run.getOwner());
				tv_joined_fabutime.setText("发布时间：" + run.getFabutime());
				adapter = new RunnerAdapter(getActivity(), userlist);
				listview.setAdapter(adapter);
			}
		} else {
			Toast.makeText(getActivity(), "网络出现问题了...", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
