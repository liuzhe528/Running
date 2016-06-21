package com.lzstudio.healthy.activity;

import java.text.DecimalFormat;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.adapter.MenuAdapter;
import com.lzstudio.healthy.fragment.HealthyFragment;
import com.lzstudio.healthy.fragment.MainFragment;
import com.lzstudio.healthy.fragment.MainFragment.ChildInter;
import com.lzstudio.healthy.fragment.RankFragment;
import com.lzstudio.healthy.fragment.SettingFragment;
import com.lzstudio.healthy.fragment.ShopFragment;
import com.lzstudio.healthy.fragment.WeatherFragment;
import com.lzstudio.healthy.fragment.YuePaoFragment;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.pedometer.StepService;
import com.lzstudio.healthy.utils.Options;
import com.lzstudio.healthy.view.residemenu.ResideMenu;
import com.lzstudio.healthy.view.residemenu.ResideMenuItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.FeedbackAgent;
import com.volley.tools.os.tbfy;
import com.volley.tools.os.tbky;
import com.volley.tools.os.tbmy;
import com.wechat.tools.br.AdSize;
import com.wechat.tools.br.AdView;
import com.wechat.tools.br.AdViewListener;
import com.wechat.tools.st.SpotManager;

public class BaseActivity extends FragmentActivity implements OnClickListener,
		ChildInter, ResponseData {
	private ResideMenu resideMenu;
	private TextView tv_title;
	private TextView tv_menu;
	private TextView tv_more;
	private FragmentManager fragmentManager;
	private Fragment content, mainFragment, healthyFragment, weatherFragment,
			rankFragment, settingFragment, shopFragment, yuepaoFragment;
	/**
	 * 判断服务是否正在运行
	 */
	private boolean mIsRunning;

	private static final String TAG = "BASE_ACTIVITY";
	private StepService mService;
	/*
	 * 计步器
	 */
	private ResideMenuItem mPedometer;
	/**
	 * 排行榜
	 */
	private ResideMenuItem mRank;
	/**
	 * 天气
	 */
	private ResideMenuItem mWeather;
	/**
	 * 设置
	 */
	private ResideMenuItem mSetting;
	/**
	 * 健康
	 */
	private ResideMenuItem mHealthy;
	/**
	 * 积分
	 */
	private ResideMenuItem mPoint;
	/**
	 * 话费
	 */
	private ResideMenuItem mShop;
	/**
	 * 约跑
	 */
	private ResideMenuItem mYuePao;
	private PopupWindow popup;
	private ListView menuList;
	// create menu items;
	private String titles[] = { "计步器", "排行榜", "天气预报", "健康知识", "设置", "赚取积分",
			"兑换话费", "约跑" };
	private int icon[] = { R.drawable.icon_pedometer, R.drawable.icon_home,
			R.drawable.icon_profile, R.drawable.icon_calendar,
			R.drawable.icon_settings, R.drawable.icon_point,
			R.drawable.icon_shop, R.drawable.icon_yuepao };

	public interface ChangeListener {
		public void stepChange(int value);

		public void distanceChange(float value);

		public void caloriesChange(float value);

		public void timeChanged(long value);
	}

	private ImageView iv_userHead;
	private TextView tv_login_username;
	private ChangeListener listener;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;

	public void setOnChangeListener(ChangeListener listener) {
		this.listener = listener;
	}

	private int paceCount = 0;
	private float calories = 0;
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	private StepService.ICallback mCallback = new StepService.ICallback() {
		public void stepsChanged(int value) {
			listener.stepChange(value);
			paceCount = value;
		}

		public void paceChanged(int value) {
		}

		public void distanceChanged(float value) {
			listener.distanceChange(value);
		}

		public void speedChanged(float value) {
		}

		public void caloriesChanged(float value) {
			listener.caloriesChange(value);
			calories = value;
		}

		public void timeChanged(long time) {
			listener.timeChanged(time);
		}
	};
	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			mService = ((StepService.StepBinder) service).getService();

			mService.registerCallback(mCallback);
			mService.reloadSettings();

		}

		public void onServiceDisconnected(ComponentName className) {
			mService = null;
		}
	};
	private SharedPreferences sp;
	private LinearLayout loginStatus;
	private Context context;
	private float points = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		context = this;
		options = Options.getListOptions();
		imageLoader.init(Options.getConfig(context));
		MobclickAgent.updateOnlineConfig(this);
		AnalyticsConfig.enableEncrypt(true);
		initView();
		setUpUmengFeedback();
		initPopup();
		sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		fragmentManager = this.getSupportFragmentManager();
		mainFragment = new MainFragment();
		fragmentManager.beginTransaction()
				.add(R.id.layout_content, mainFragment).commit();
		content = mainFragment;
		if (!TextUtils.isEmpty(sp.getString("phoneNumber", ""))) {
			tbmy.getInstance(this).beqg(new tbky() {
				@Override
				public void behg(float arg0) {
					points = arg0;
					HttpClientUtils.getInstance().pointJson(BaseActivity.this,
							Url.Point_Host, sp.getString("phoneNumber", ""),
							System.currentTimeMillis(), (int) points, 2);
				}
			});
		}
		setSpotAd();
		showBanner();
	}

	private MenuAdapter adapter;

	private void initPopup() {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.menu_list, null);
		menuList = (ListView) view.findViewById(R.id.menu_list);
		popup = new PopupWindow(view);
		popup.setFocusable(true);// 加上这个popupwindow中的ListView才可以接收点击事件
		adapter = new MenuAdapter(this);
		menuList.setAdapter(adapter);
		menuList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				popup.dismiss();
				switch (position) {
				case 0:// 分享成绩
					showShare();
					break;
				case 1:// 给个好评
					Uri uri = Uri.parse("market://details?id="
							+ getPackageName());
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					break;
				case 2:// 意见反馈
					fb.startFeedbackActivity();
					break;
				case 3:// 使用说明
					Intent aboutIntent = new Intent(context,
							AboutUsActivity.class);
					startActivity(aboutIntent);
					break;
				}
			}
		});
		menuList.measure(View.MeasureSpec.UNSPECIFIED,
				View.MeasureSpec.UNSPECIFIED);
		popup.setWidth(menuList.getMeasuredWidth());
		popup.setHeight(LayoutParams.WRAP_CONTENT);
		// 点back键和其他地方使其消失,设置了这个才能触发
		popup.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.menu_bg));
		popup.setOutsideTouchable(true);
	}

	private void showShare() {
		ShareSDK.initSDK(this);
		OnekeyShare oks = new OnekeyShare();
		// 关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("我今天走了" + paceCount + "步，" + "燃烧了"
				+ decimalFormat.format(calories) + "g卡路里");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("健康生活,坚持运动,你们能超过我吗？");
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://a.app.qq.com/o/simple.jsp?pkgname=com.lzstudio.healthy");
		oks.setImageUrl("http://119.29.247.205:8888/RunningServer/running.png");
		// oks.setImagePath(uri.toString());// 确保SDcard下面存在此张图片
		// 分享时Notification的图标和文字 2.5.9以后的版本不调用此方法
		// oks.setNotification(R.drawable.ic_launcher,
		// getString(R.string.app_name));
		// titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		// oks.setTitleUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		// oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		// oks.setSite(getString(R.string.app_name));
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		// oks.setSiteUrl("http://sharesdk.cn");
		// 启动分享GUI
		oks.show(this);
	}

	public StepService getStepService() {
		return mService;
	}

	FeedbackAgent fb;

	private void setUpUmengFeedback() {
		fb = new FeedbackAgent(this);
		// check if the app developer has replied to the feedback or not.
		fb.sync();
		fb.openAudioFeedback();
		fb.openFeedbackPush();
		// fb.setWelcomeInfo();
		// fb.setWelcomeInfo("Welcome to use umeng feedback app");
		// FeedbackPush.getInstance(this).init(true);
		// PushAgent.getInstance(this).setPushIntentServiceClass(MyPushIntentService.class);

		new Thread(new Runnable() {
			@Override
			public void run() {
				boolean result = fb.updateUserInfo();
			}
		}).start();
	}

	private void showBanner() {
		// 实例化广告条
		AdView adView = new AdView(this, AdSize.FIT_SCREEN);
		// 获取要嵌入广告条的布局
		LinearLayout adLayout = (LinearLayout) findViewById(R.id.adLayout);
		// 监听广告条接口
		adView.setAdListener(new AdViewListener() {

			@Override
			public void onSwitchedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "广告条切换");
			}

			@Override
			public void onReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告成功");

			}

			@Override
			public void onFailedToReceivedAd(AdView arg0) {
				Log.i("YoumiAdDemo", "请求广告失败");
			}
		});
		adLayout.removeAllViews();
		// 将广告条加入到布局中
		adLayout.addView(adView);

	}

	private void setSpotAd() {
		// 插播接口调用
		// 加载插播资源
		SpotManager.getInstance(context).loadSpotAds();
		// 插屏出现动画效果，0:ANIM_NONE为无动画，1:ANIM_SIMPLE为简单动画效果，2:ANIM_ADVANCE为高级动画效果
		SpotManager.getInstance(context).setAnimationType(
				SpotManager.ANIM_ADVANCE);
		// 设置插屏动画的横竖屏展示方式，如果设置了横屏，则在有广告资源的情况下会是优先使用横屏图。
		SpotManager.getInstance(context).setSpotOrientation(
				SpotManager.ORIENTATION_PORTRAIT);
		SpotManager.getInstance(context).showSpotAds(context);
	}

	@Override
	public void onBackPressed() {

		// 如果有需要，可以点击后退关闭插播广告。
		if (!SpotManager.getInstance(context).disMiss()) {
			// 弹出退出窗口，可以使用自定义退屏弹出和回退动画,参照demo,若不使用动画，传入-1
			super.onBackPressed();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 查询积分
		float p = tbmy.getInstance(this).bepg();
		// 扣除积分
		tbmy.getInstance(this).bfrg(p);
		// 增加积分
		p = sp.getInt("points", 0);
		tbmy.getInstance(this).bbjg(p);
		String username = sp.getString("username", "未登录");
		tv_login_username.setText(username);
		String path = sp.getString("headImgSource", "");
		if (TextUtils.isEmpty(path)) {
			iv_userHead.setImageResource(R.drawable.ic_login_head);
		} else {
			imageLoader.displayImage(path, iv_userHead, options);
		}
		mIsRunning = isServiceRunning();
		if (mIsRunning) {
			bindStepService();
		}
		MobclickAgent.onResume(this);
	}

	public void startStepService() {
		if (!mIsRunning) {
			Log.i(TAG, "[SERVICE] Start");
			mIsRunning = true;
			startService(new Intent(this, StepService.class));
		}
	}

	public void bindStepService() {
		Log.i(TAG, "[SERVICE] Bind");
		bindService(new Intent(this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

	public void unbindStepService() {
		Log.i(TAG, "[SERVICE] Unbind");
		unbindService(mConnection);
	}

	public void stopStepService() {
		Log.i(TAG, "[SERVICE] Stop");
		if (mService != null) {
			Log.i(TAG, "[SERVICE] stopService");
			stopService(new Intent(this, StepService.class));
		}
		mIsRunning = false;
	}

	/**
	 * 判断服务是否在运行
	 * 
	 * @return
	 */
	private boolean isServiceRunning() {
		ActivityManager activityMannager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningServiceInfo> serviceList = activityMannager
				.getRunningServices(200);
		if (!(serviceList.size() > 0)) {
			return false;
		}
		for (RunningServiceInfo info : serviceList) {
			if (info.service.getClassName().equals(
					"com.lzstudio.healthy.pedometer.StepService")) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 按两次退出
	 */
	long curr = 0;
	long last = 0;

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (content == mainFragment) {
				curr = System.currentTimeMillis();
				if (curr - last < 1000) {
					finish();
				} else {
					Toast.makeText(this, R.string.press_again_exit,
							Toast.LENGTH_SHORT).show();
					last = curr;
				}
			} else {
				backToMainFragment();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MENU && !popup.isShowing()) {
			popup.showAsDropDown(tv_more, 0, 25);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void backToMainFragment() {
		switchContent(content, mainFragment);
		changeTvMore(R.drawable.titlebar_more);
		setFragmentTitle("计步器");
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		if (mIsRunning) {
			unbindStepService();
		}
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onStop() {
		SpotManager.getInstance(context).onStop();
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		tbfy.getInstance(this).bdtg();
		SpotManager.getInstance(context).onDestroy();
		super.onDestroy();
	}

	/**
	 * 初始化控件
	 */
	private void initView() {
		initResideView();
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_more = (TextView) this.findViewById(R.id.tv_more);
		changeTvMoreListener();
		tv_title.setText(R.string.main_title);
		tv_menu = (TextView) this.findViewById(R.id.tv_menu);
		tv_menu.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!resideMenu.isOpened()) {
					resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
				} else {
					resideMenu.closeMenu();
				}
			}
		});
	}

	/**
	 * 初始化resideView
	 */
	private void initResideView() {
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setScaleValue(0.7f);
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

		mPedometer = new ResideMenuItem(this, icon[0], titles[0]);
		mRank = new ResideMenuItem(this, icon[1], titles[1]);
		mWeather = new ResideMenuItem(this, icon[2], titles[2]);
		mHealthy = new ResideMenuItem(this, icon[3], titles[3]);
		mSetting = new ResideMenuItem(this, icon[4], titles[4]);
		mPoint = new ResideMenuItem(this, icon[5], titles[5]);
		mShop = new ResideMenuItem(this, icon[6], titles[6]);
		mYuePao = new ResideMenuItem(this, icon[7], titles[7]);
		// 添加点击事件
		mPedometer.setOnClickListener(this);
		mRank.setOnClickListener(this);
		mWeather.setOnClickListener(this);
		mHealthy.setOnClickListener(this);
		mSetting.setOnClickListener(this);
		mPoint.setOnClickListener(this);
		mShop.setOnClickListener(this);
		mYuePao.setOnClickListener(this);
		// 将条目添加到菜单上
		resideMenu.addMenuItem(mPedometer, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mYuePao, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mRank, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mWeather, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mHealthy, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mSetting, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mPoint, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(mShop, ResideMenu.DIRECTION_LEFT);
		loginStatus = resideMenu.getLoginStatus();
		iv_userHead = resideMenu.getHeadImageView();
		tv_login_username = resideMenu.getUserNameTextView();
		loginStatus.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mPedometer) {
			switchContent(content, mainFragment);
			changeTvMore(R.drawable.titlebar_more);
			setFragmentTitle("计步器");
		} else if (v == mYuePao) {
			if (!TextUtils.isEmpty(sp.getString("phoneNumber", ""))) {
				if (yuepaoFragment == null) {
					yuepaoFragment = new YuePaoFragment();
				}
				switchContent(content, yuepaoFragment);
				changeTvMore(R.drawable.titlebar_more);
				setFragmentTitle("约跑");
			} else {
				Toast.makeText(this, "您还未登录", Toast.LENGTH_SHORT).show();
			}
		} else if (v == mRank) {
			if (rankFragment == null) {
				rankFragment = new RankFragment();
			}
			switchContent(content, rankFragment);
			changeTvMore(R.drawable.titlebar_more);
			setFragmentTitle("排行榜");
		} else if (v == mWeather) {
			if (weatherFragment == null) {
				weatherFragment = new WeatherFragment();
			}
			switchContent(content, weatherFragment);
			changeTvMore(R.drawable.btn_local);
		} else if (v == mHealthy) {
			if (healthyFragment == null) {
				healthyFragment = new HealthyFragment();
			}
			switchContent(content, healthyFragment);
			changeTvMore(R.drawable.titlebar_more);
			setFragmentTitle("健康知识");
		} else if (v == mSetting) {
			if (settingFragment == null) {
				settingFragment = new SettingFragment();
			}
			switchContent(content, settingFragment);
			changeTvMore(R.drawable.titlebar_more);
			setFragmentTitle("设置");
		} else if (v == loginStatus) {
			boolean hasLogin = sp.getBoolean("isSavePasswordChecked", false);
			if (hasLogin) {// 跳转到用户设置界面
				Intent intent = new Intent(BaseActivity.this,
						SettingActivity.class);
				startActivity(intent);
			} else {// 跳转到登录注册界面
				Intent intent = new Intent(BaseActivity.this,
						BaseLoginActivity.class);
				startActivity(intent);
			}
		} else if (v == mPoint) {
			if (!TextUtils.isEmpty(sp.getString("phoneNumber", ""))) {
				// tbmy.getInstance(this).bbjg(50f);
				tbfy.getInstance(this).bfkg();
			} else {
				Toast.makeText(this, "您还未登录", Toast.LENGTH_SHORT).show();
			}
		} else if (v == mShop) {
			if (!TextUtils.isEmpty(sp.getString("phoneNumber", ""))) {
				if (shopFragment == null) {
					shopFragment = new ShopFragment();
				}
				switchContent(content, shopFragment);
				changeTvMore(R.drawable.titlebar_more);
				setFragmentTitle("兑换话费");
			} else {
				Toast.makeText(this, "您还未登录", Toast.LENGTH_SHORT).show();
			}
		}
		resideMenu.closeMenu();
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		return resideMenu.dispatchTouchEvent(ev);
	}

	private void changeTvMore(int id) {
		Drawable drawable = getResources().getDrawable(id);
		drawable.setBounds(0, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		tv_more.setCompoundDrawables(null, null, drawable, null);
		if (id != R.drawable.btn_local) {
			changeTvMoreListener();
		}
	}

	private void changeTvMoreListener() {
		tv_more.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (popup.isShowing()) {
					popup.dismiss();
				} else {
					popup.showAsDropDown(tv_more, 0, 25);
				}
			}
		});
	}

	/**
	 * 替换fragment
	 * 
	 * @param from
	 * @param to
	 */
	private void switchContent(Fragment from, Fragment to) {
		if (content != to) {
			content = to;
			FragmentTransaction transaction = fragmentManager
					.beginTransaction();
			if (!to.isAdded()) { // 先判断是否被add过
				transaction.hide(from).add(R.id.layout_content, to).commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	public void setFragmentTitle(String title) {
		tv_title.setText(title);
	}

	public TextView getTvMore() {
		return tv_more;
	}

	public TextView getTvTitle() {
		return tv_title;
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == -1) {
			Log.i(TAG, "积分改变联网失败");
			HttpClientUtils.getInstance().pointJson(BaseActivity.this,
					Url.Point_Host, sp.getString("phoneNumber", ""),
					System.currentTimeMillis(), (int) points, 2);
		} else if (id == 2) {
			JSONObject json;
			try {
				json = new JSONObject(result);
				if (json.getInt("result_code") == 1) {
					Log.i(TAG, "积分改变成功");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
