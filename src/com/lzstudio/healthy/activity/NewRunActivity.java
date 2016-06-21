package com.lzstudio.healthy.activity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;

public class NewRunActivity extends Activity implements OnClickListener,
		ResponseData {
	private EditText et_title, et_address, et_desc;
	private Button bt_new;
	private TimePicker tp_time;
	private TextView tv_location, tv_title, tv_back, tv_loading;
	private SharedPreferences sp;
	private RelativeLayout rl_loading;
	public LocationClient mLocationClient = null;
	private LocationMode tempMode = LocationMode.Hight_Accuracy;
	public BDLocationListener myListener = new MyLocationListener();
	private String tempcoor = "gcj02";
	private String address = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.new_run_activity);
		sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
		initLocation();
		initView();
		if (CommonUtil.isNetworkAvailable(this) != 0) {
			mLocationClient.start();
		} else {
			Toast.makeText(this, "手机没有网络...", Toast.LENGTH_SHORT).show();
		}
	}

	private void initLocation() {
		mLocationClient = new LocationClient(this); // 声明LocationClient类
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
			address = location.getProvince() + location.getCity();
			tv_location.setText(address);
			mLocationClient.stop();
		}
	}

	@Override
	protected void onStop() {
		if (mLocationClient.isStarted()) {
			mLocationClient.stop();
		}
		super.onStop();
	}

	private void initView() {
		this.et_title = (EditText) this.findViewById(R.id.et_new_run_title);
		this.et_address = (EditText) this.findViewById(R.id.et_new_run_address);
		this.et_desc = (EditText) this.findViewById(R.id.et_new_run_desc);
		this.tv_location = (TextView) this
				.findViewById(R.id.tv_new_run_location);
		this.tp_time = (TimePicker) this.findViewById(R.id.tp_new_run_time);
		this.tp_time.setIs24HourView(true);
		this.tv_title = (TextView) this.findViewById(R.id.tv_title);
		this.tv_title.setText("发布跑步信息");
		this.tv_back = (TextView) this.findViewById(R.id.tv_back);
		this.bt_new = (Button) this.findViewById(R.id.bt_new_run);
		this.rl_loading = (RelativeLayout) this
				.findViewById(R.id.rl_newrun_loading);
		this.tv_loading = (TextView) this.findViewById(R.id.tv_loading);
		this.tv_loading.setText("正在发布...");
		this.bt_new.setOnClickListener(this);
		this.tv_back.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_new_run:
			String title = et_title.getText().toString().trim();
			String addr = et_address.getText().toString().trim();
			String desc = et_desc.getText().toString().trim();
			String hour = tp_time.getCurrentHour().toString();
			String minutes = tp_time.getCurrentMinute().toString();
			if (TextUtils.isEmpty(title)) {
				Toast.makeText(this, "标题不能为空", Toast.LENGTH_SHORT).show();
				return;
			} else if (TextUtils.isEmpty(addr)) {
				Toast.makeText(this, "地址不能为空", Toast.LENGTH_SHORT).show();
				return;
			} else if (TextUtils.isEmpty(desc)) {
				Toast.makeText(this, "详细内容不能为空", Toast.LENGTH_SHORT).show();
				return;
			}
			int selectHour = tp_time.getCurrentHour();
			int selectMin = tp_time.getCurrentMinute();

			int nowHour = Calendar.HOUR_OF_DAY;
			int nowMin = Calendar.MINUTE;
			if (selectHour < nowHour
					|| (selectHour == nowHour && selectMin <= nowMin)) {
				Toast.makeText(this, "时间不能小于当前时间", Toast.LENGTH_SHORT).show();
				return;
			}
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
			Date date = new Date(System.currentTimeMillis());
			String yuedingtime = format.format(date);
			if (selectMin < 10) {
				minutes = "0" + minutes;
			}
			if (selectHour < 10) {
				hour = "0" + hour;
			}
			yuedingtime = yuedingtime + "  " + hour + ":" + minutes;
			Run run = new Run();
			run.setTitle(title);
			run.setAddress(address + addr);
			run.setDescribe(desc);
			run.setYuedingtime(yuedingtime);
			run.setOwner(sp.getString("username", ""));
			String phoneNumber = sp.getString("phoneNumber", "");
			rl_loading.setVisibility(View.VISIBLE);
			HttpClientUtils.getInstance().newRun(this, Url.NEW_RUN, run,
					phoneNumber);
			break;
		case R.id.tv_back:
			this.finish();
			break;
		}
	}

	@Override
	public void getResponseData(int id, String result) {
		rl_loading.setVisibility(View.GONE);
		if (id == 1) {
			String fabutime = NewsParser.getInstance().newRunJsonParser(result);
			if (TextUtils.isEmpty(fabutime)) {
				Toast.makeText(this, "服务器出错，请稍后再试...", Toast.LENGTH_SHORT)
						.show();
			} else {
				sp.edit().putString("fabutime", fabutime).commit();
				Toast.makeText(getApplicationContext(), "发布成功",
						Toast.LENGTH_SHORT).show();
				this.finish();
			}
		} else {
			Toast.makeText(this, "网络出现问题了...", Toast.LENGTH_SHORT).show();
		}
	}
}
