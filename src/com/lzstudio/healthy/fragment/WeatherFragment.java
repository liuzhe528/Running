package com.lzstudio.healthy.fragment;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseActivity;
import com.lzstudio.healthy.activity.ChooseCityActivity;
import com.lzstudio.healthy.adapter.MyViewPagerAdapter;
import com.lzstudio.healthy.adapter.WeatherAdapter;
import com.lzstudio.healthy.bean.WeatherModle;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.VolleyUtils;
import com.lzstudio.healthy.http.json.WeatherListJson;
import com.lzstudio.healthy.http.utils.CommonUtil;
import com.lzstudio.healthy.utils.ACache;
import com.umeng.analytics.MobclickAgent;

public class WeatherFragment extends Fragment implements ResponseData {
	private TextView tv_title;
	private TextView tv_location;
	private ImageView iv_weather;
	private TextView tv_weather_date;
	private TextView tv_weather;
	private TextView tv_weather_temp;
	private TextView tv_weather_wind;
	private ViewPager viewPager;
	private RelativeLayout mLayout;
	private GridView view1, view2;
	private View weatherGridView1, weatherGridView2;
	private WeatherAdapter weatherAdapter1, weatherAdapter2;
	private List<View> views = new ArrayList<View>();
	public static final int REQUEST_CODE = 1000;
	private static final int GET_MESSAGE_SUCCESS = 10;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case GET_MESSAGE_SUCCESS:
				String res = (String) msg.obj;
				getResult(res);
				break;
			}
		};
	};
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	private String tempcoor = "gcj02";
	private String titleName = "";
	private View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		try {
			view = inflater.inflate(R.layout.layout_tianqi, container, false);
			initView();
			initViewPager();
			initLocation();
			if (CommonUtil.isNetworkAvailable(getMyActivity()) != 0) {
				mLocationClient.start();
			} else {
				CommonUtil.showToast(getMyActivity(), "没有网络....");
				titleName = getCacheStr("titleName");
				tv_title.setText(titleName + "天气");
				setBack(titleName);
				loadData(getWeatherUrl(titleName));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			try {
				if (CommonUtil.isNetworkAvailable(getMyActivity()) != 0) {
					mLocationClient.start();
				} else {
					CommonUtil.showToast(getMyActivity(), "没有网络....");
					titleName = getCacheStr("titleName");
					tv_title.setText(titleName + "天气");
					setBack(titleName);
					loadData(getWeatherUrl(titleName));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化百度定位
	 */
	private void initLocation() {
		mLocationClient = new LocationClient(getMyActivity()
				.getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(tempMode);// 设置定位模式
		option.setCoorType(tempcoor);// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 1000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为1000ms
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
	}

	class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			try {
				String city = location.getCity();
				String name[] = city.split("市");
				titleName = name[0];
				setCacheStr("titleName", titleName);
				tv_title.setText(titleName + "天气");
				setBack(titleName);
				loadData(getWeatherUrl(titleName));
				mLocationClient.stop();
			} catch (UnsupportedEncodingException e) {
				if (mLocationClient.isStarted()) {
					mLocationClient.stop();
				}
				e.printStackTrace();
			}
		}
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		viewPager = (ViewPager) view.findViewById(R.id.viewpager);
		iv_weather = (ImageView) view.findViewById(R.id.weatherImage);
		tv_weather = (TextView) view.findViewById(R.id.weather);
		tv_weather_date = (TextView) view.findViewById(R.id.weather_date);
		tv_weather_temp = (TextView) view.findViewById(R.id.weatherTemp);
		tv_weather_wind = (TextView) view.findViewById(R.id.wind);
		mLayout = (RelativeLayout) view.findViewById(R.id.layout);
		tv_location = ((BaseActivity) getActivity()).getTvMore();
		tv_title = ((BaseActivity) getActivity()).getTvTitle();
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {
					((BaseActivity) getActivity()).getResideMenu()
							.clearIgnoredViewList();
				} else {
					((BaseActivity) getActivity()).getResideMenu()
							.addIgnoredView(viewPager);
				}
				viewPager.setCurrentItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
		tv_location.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getMyActivity(),
						ChooseCityActivity.class);
				startActivityForResult(intent, REQUEST_CODE);
			}
		});
	}

	/**
	 * 初始化viewPager
	 */
	private void initViewPager() {
		weatherGridView1 = LayoutInflater.from(getMyActivity()).inflate(
				R.layout.gridview_weather, null);
		weatherGridView2 = LayoutInflater.from(getMyActivity()).inflate(
				R.layout.gridview_weather, null);
		view1 = (GridView) weatherGridView1.findViewById(R.id.gridView);
		view2 = (GridView) weatherGridView2.findViewById(R.id.gridView);
		weatherAdapter1 = new WeatherAdapter(getMyActivity());
		weatherAdapter2 = new WeatherAdapter(getMyActivity());
		view1.setAdapter(weatherAdapter1);
		view2.setAdapter(weatherAdapter2);
		views.add(weatherGridView1);
		views.add(weatherGridView2);
		viewPager.setOffscreenPageLimit(1);
		MyViewPagerAdapter mAdapter = new MyViewPagerAdapter(views);
		viewPager.setAdapter(mAdapter);
		viewPager.setCurrentItem(0);
	}

	/**
	 * 获取天气预报数据
	 * 
	 * @param weatherUrl
	 */
	private void loadData(String weatherUrl) {
		if (CommonUtil.isNetworkAvailable(getMyActivity()) != 0) {
			VolleyUtils.getVolleyData(weatherUrl, getMyActivity(), this);
		} else {
			CommonUtil.showToast(getMyActivity(), "没有网络...");
			String result = getCacheStr("WeatherActivity");
			if (!TextUtils.isEmpty(result)) {
				getResult(result);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE) {
			if (data != null) {
				String titleName = data.getStringExtra("cityname");
				setCacheStr("titleName", titleName);
				if (!"".equals(titleName)) {
					tv_title.setText(titleName + "天气");
					setBack(titleName);
					try {
						loadData(getWeatherUrl(titleName));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * 解析Json数据
	 * 
	 * @param result
	 */
	private void getResult(String result) {
		setCacheStr("WeatherActivity", result);
		List<WeatherModle> weatherModles = WeatherListJson.instance(
				getMyActivity()).readJsonWeatherListModles(result);
		if (weatherModles.size() > 0) {
			setWeather(weatherModles.get(0));
			weatherAdapter1.clear();
			weatherAdapter2.clear();
			weatherAdapter1.appendList(weatherModles.subList(1, 4));
			weatherAdapter2.appendList(weatherModles.subList(4,
					weatherModles.size()));
		} else {
			CommonUtil.showToast(getMyActivity(), "错误");
		}
	}

	/**
	 * 设置当天的天气
	 * 
	 * @param weatherModle
	 */
	private void setWeather(WeatherModle weatherModle) {
		tv_weather.setText(weatherModle.getWeather());
		tv_weather_date.setVisibility(View.VISIBLE);
		tv_weather_date.setText(weatherModle.getDate());
		tv_weather_temp.setText(weatherModle.getTemperature());
		tv_weather_wind.setText(weatherModle.getWind());
		setWeatherImage(iv_weather, weatherModle.getWeather());
	}

	public void setWeatherImage(ImageView mWeatherImage, String weather) {
		if (weather.equals("多云") || weather.equals("多云转阴")
				|| weather.equals("多云转晴")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_duoyun);
		} else if (weather.equals("中雨") || weather.equals("中到大雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
		} else if (weather.equals("雷阵雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
		} else if (weather.equals("阵雨") || weather.equals("阵雨转多云")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
		} else if (weather.equals("暴雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_baoxue);
		} else if (weather.equals("暴雨")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_baoyu);
		} else if (weather.equals("大暴雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
		} else if (weather.equals("大雪")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_daxue);
		} else if (weather.equals("大雨") || weather.equals("大雨转中雨")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_dayu);
		} else if (weather.equals("雷阵雨冰雹")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
		} else if (weather.equals("晴")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_qing);
		} else if (weather.equals("沙尘暴")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
		} else if (weather.equals("特大暴雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
		} else if (weather.equals("雾") || weather.equals("雾霾")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_wu);
		} else if (weather.equals("小雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
		} else if (weather.equals("小雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
		} else if (weather.equals("阴")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_yin);
		} else if (weather.equals("雨夹雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
		} else if (weather.equals("阵雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
		} else if (weather.equals("中雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
		}
	}

	/**
	 * 根据城市名字获取天气预报的url地址
	 * 
	 * @param titleName
	 *            城市的名字
	 * @return 返回城市所对应的url地址
	 * @throws UnsupportedEncodingException
	 */
	private String getWeatherUrl(String titleName)
			throws UnsupportedEncodingException {
		String urlString = Url.WeatherHost
				+ URLEncoder.encode(titleName, "utf-8");
		return urlString;
	}

	/**
	 * 设置天气预报背景图片
	 * 
	 * @param cityName
	 *            城市名字
	 */
	public void setBack(String cityName) {
		if (cityName.equals("北京")) {
			mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_beijin_bg);
		} else if (cityName.equals("上海")) {
			mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_shanghai_bg);
		} else if (cityName.equals("广州")) {
			mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_guangzhou_bg);
		} else if (cityName.equals("深圳")) {
			mLayout.setBackgroundResource(R.drawable.biz_plugin_weather_shenzhen_bg);
		} else {
			mLayout.setBackgroundResource(R.drawable.biz_news_local_weather_bg_big);
		}
	}

	@Override
	public void getResponseData(int id, String result) {
		Message msg = Message.obtain();
		msg.what = GET_MESSAGE_SUCCESS;
		msg.obj = result;
		handler.sendMessage(msg);
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

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen");
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}
}
