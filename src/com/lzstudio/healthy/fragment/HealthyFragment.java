package com.lzstudio.healthy.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseActivity;
import com.lzstudio.healthy.adapter.HealthyAdapter;
import com.lzstudio.healthy.bean.Colums;
import com.lzstudio.healthy.view.tabindicator.ViewPagerIndicator;
import com.lzstudio.healthy.view.tabindicator.ViewPagerIndicator.PageChangeListener;
import com.umeng.analytics.MobclickAgent;

public class HealthyFragment extends Fragment {
	private View view;
	private ViewPagerIndicator indicator;
	private ViewPager viewpager;
	private List<Colums> colums;
	private String[] names = { "新闻", "保健", "常见病", "养生", "心理", "挑食", "用药", "两性",
			"疑难病" };
	private int[] ids = { 562, 570, 578, 569, 568, 567, 580, 663, 579 };
	private List<Fragment> mTabContents = new ArrayList<Fragment>();
	private HealthyAdapter mAdapter;
	private Fragment newFragment;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.healthy_fragment, container, false);
		colums = new ArrayList<Colums>();
		((BaseActivity) getActivity()).setFragmentTitle("健康知识");
		initView();
		initDatas();
		indicator.setTabItemTitles(colums);
		viewpager.setAdapter(mAdapter);
		indicator.setViewPager(viewpager, 0);
		indicator.setOnPageChangeListener(new PageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					((BaseActivity) getActivity()).getResideMenu()
							.clearIgnoredViewList();
				} else {
					((BaseActivity) getActivity()).getResideMenu()
							.addIgnoredView(viewpager);
				}
				viewpager.setCurrentItem(position);
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
		return view;
	}

	/**
	 * 获取条目的数量
	 */
	private void initDatas() {
		mTabContents.clear();
		for (int i = 0; i < names.length; i++) {
			Colums colum = new Colums();
			colum.setId(ids[i]);
			colum.setName(names[i]);
			colums.add(colum);
			newFragment = new BaseFragment(ids[i] + "");
			mTabContents.add(newFragment);
		}
		mAdapter = new HealthyAdapter(getChildFragmentManager(), mTabContents);
	}

	private void initView() {
		indicator = (ViewPagerIndicator) view.findViewById(R.id.id_indicator);
		viewpager = (ViewPager) view.findViewById(R.id.id_vp);
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
