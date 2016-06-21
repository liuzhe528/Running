package com.lzstudio.healthy.activity;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.volley.tools.tafy;
import com.volley.tools.os.tbfy;
import com.wechat.tools.AdManager;
import com.wechat.tools.st.SplashView;
import com.wechat.tools.st.SpotDialogListener;
import com.wechat.tools.st.SpotManager;

public class SplashSpotActivity extends Activity implements ResponseData {

	SplashView splashView;
	Context context;
	View splash;
	RelativeLayout splashLayout;
	private SharedPreferences sp;
	private String phoneNumber;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		context = this;
		// 初始化接口，应用启动的时候调用
		// 参数：appId, appSecret, 调试模式
		AdManager.getInstance(context).init("1199104dfc2bf47f",
				"839cc61a258697b1", false);
		// 有米积分墙
		tafy.getInstance(this).init("1199104dfc2bf47f", "839cc61a258697b1");
		tbfy.getInstance(this).bdug();
		// 第二个参数传入目标activity，或者传入null，改为setIntent传入跳转的intent
		splashView = new SplashView(context, null);
		// 设置是否显示倒数
		splashView.setShowReciprocal(true);
		// 隐藏关闭按钮
		splashView.hideCloseBtn(false);

		Intent intent = new Intent(context, BaseActivity.class);
		splashView.setIntent(intent);
		splashView.setIsJumpTargetWhenFail(true);

		splash = splashView.getSplashView();
		setContentView(R.layout.splash_activity);
		boolean shouldLogin = sp.getBoolean("isSavePasswordChecked", false);
		phoneNumber = sp.getString("phoneNumber", "");
		password = sp.getString("password", "");
		if (shouldLogin && !TextUtils.isEmpty(phoneNumber)
				&& !TextUtils.isEmpty(password)) {
			HttpClientUtils.getInstance().loginJson(this, Url.LOGIN_HOST,
					phoneNumber, password);
		}
		splashLayout = ((RelativeLayout) findViewById(R.id.splashview));
		splashLayout.setVisibility(View.GONE);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				-1, -1);
		params.addRule(RelativeLayout.ABOVE, R.id.cutline);
		splashLayout.addView(splash, params);

		SpotManager.getInstance(context).showSplashSpotAds(context, splashView,
				new SpotDialogListener() {

					@Override
					public void onShowSuccess() {
						splashLayout.setVisibility(View.VISIBLE);
						splashLayout.startAnimation(AnimationUtils
								.loadAnimation(context,
										R.anim.pic_enter_anim_alpha));
						Log.d("youmisdk", "展示成功");
					}

					@Override
					public void onShowFailed() {
						Log.d("youmisdk", "展示失败");
					}

					@Override
					public void onSpotClosed() {
						Log.d("youmisdk", "展示关闭");
					}

					@Override
					public void onSpotClick() {
						Log.i("YoumiAdDemo", "插屏点击");
					}
				});

		// 2.简单调用方式
		// 如果没有特殊要求，简单使用此句即可实现插屏的展示
		// SpotManager.getInstance(context).showSplashSpotAds(context,
		// MainActivity.class);

	}

	// 请务必加上词句，否则进入网页广告后无法进去原sdk
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == 10045) {
			Intent intent = new Intent(context, BaseActivity.class);
			startActivity(intent);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		// 取消后退键
	}

	@Override
	protected void onResume() {

		/**
		 * 设置为竖屏
		 */
		if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		}
		super.onResume();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// land
		} else if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			// port
		}
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == 1) {
			JSONObject json;
			try {
				json = new JSONObject(result);
				int result_code = json.getInt("result_code");
				switch (result_code) {
				case 1:
					JSONObject body = json.getJSONObject("body");
					String username = body.getString("username");
					String sex = body.getString("sex");
					int age = body.getInt("age");
					String headImagSource = body.getString("headImgSource");
					int points = body.getInt("points");
					long paceCount = body.getLong("paceCount");
					Editor et = sp.edit();
					et.putString("username", username);
					et.putString("sex", sex);
					et.putString("phoneNumber", phoneNumber);
					et.putString("password", password);
					et.putInt("age", age);
					et.putString("headImgSource", headImagSource);
					et.putLong("paceCount", paceCount);
					et.putInt("points", points);
					et.commit();
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
