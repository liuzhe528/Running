package com.lzstudio.healthy.fragment;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.NewRunActivity;
import com.umeng.analytics.MobclickAgent;

public class YuePaoFragment extends Fragment implements OnClickListener {
	private View view;
	/**
	 * 正在进行
	 */
	private TextView mOnGoing;
	/**
	 * 我参加的
	 */
	private TextView mJoin;
	/**
	 * 我要发布
	 */
	private Button mAdd;
	private FragmentManager fragmentManager;
	private Fragment runListFragment, joinedFragment, content;
	private SharedPreferences sp;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.yuepao_fragment, container, false);
		sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		initView();
		fragmentManager = getChildFragmentManager();
		runListFragment = new RunListFragment();
		joinedFragment = new JoinedFragment();
		content = runListFragment;
		fragmentManager.beginTransaction()
				.replace(R.id.yuepao_content, content).commit();
		return view;
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		mOnGoing = (TextView) view.findViewById(R.id.yuepao_tv_coming);
		mOnGoing.setOnClickListener(this);
		mJoin = (TextView) view.findViewById(R.id.yuepao_tv_join);
		mJoin.setOnClickListener(this);
		mAdd = (Button) view.findViewById(R.id.yuepao_add);
		mAdd.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.yuepao_add:
			String fabutime = sp.getString("fabutime", "");
			SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
			String time = format.format(new Date());
			if (fabutime.equals(time)) {
				Toast.makeText(getActivity(), "每天只能发布一条跑步信息",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent(getActivity(), NewRunActivity.class);
			startActivity(intent);
			break;
		case R.id.yuepao_tv_coming:
			mJoin.setBackgroundColor(0xff6699ff);
			mOnGoing.setBackgroundColor(0xffdddddd);
			switchFragment(joinedFragment, runListFragment);
			break;
		case R.id.yuepao_tv_join:
			mOnGoing.setBackgroundColor(0xff6699ff);
			mJoin.setBackgroundColor(0xffdddddd);
			switchFragment(runListFragment, joinedFragment);
			break;
		}
	}

	private void switchFragment(Fragment from, Fragment to) {
		if (content != to) {
			content = to;
			FragmentTransaction mfragmentTransaction = fragmentManager
					.beginTransaction();
			if (!to.isAdded()) {
				mfragmentTransaction.hide(from).add(R.id.yuepao_content, to)
						.commit();
			} else {
				mfragmentTransaction.hide(from).show(to).commit();
			}
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
}
