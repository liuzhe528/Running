package com.lzstudio.healthy.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseLoginActivity;
import com.lzstudio.healthy.utils.ConstrantValue;
import com.umeng.analytics.MobclickAgent;

public class ValidateFragment extends Fragment implements OnClickListener {
	private EditText et_phoneNumber;
	private EditText et_validateNumber;
	private Button bt_getvalidateNubmer;
	private Button bt_next;
	private String phoneNumber;
	private View view;
	private RelativeLayout register_loading;
	private static final int BT_CANNOT_CLICK = 10;
	private static final int VALIDATE_SUCCESS = 20;
	private static final int VALIDATE_FAILED = 15;
	private int time = 60;
	private BaseLoginActivity baseLoginActivity;
	private ValidateChild listener;

	public void setValidateChildListener(ValidateChild listener) {
		this.listener = listener;
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case BT_CANNOT_CLICK:
				if (time >= 0) {
					bt_getvalidateNubmer.setText(time + "");
					time--;
					handler.sendEmptyMessageDelayed(BT_CANNOT_CLICK, 1000);
				} else {
					time = 60;
					bt_getvalidateNubmer
							.setText(R.string.register_getvalidatenum);
					bt_getvalidateNubmer.setClickable(true);
				}
				break;
			case VALIDATE_SUCCESS:
				register_loading.setVisibility(View.GONE);
				bt_getvalidateNubmer.setClickable(true);
				bt_next.setClickable(true);
				Toast.makeText(baseLoginActivity, "验证成功", Toast.LENGTH_SHORT)
						.show();
				baseLoginActivity
						.getSharedPreferences("login", Context.MODE_PRIVATE)
						.edit().putString("phoneNumber", phoneNumber).commit();
				listener.changeToRegisterFragment();
				break;
			case VALIDATE_FAILED:
				register_loading.setVisibility(View.GONE);
				bt_getvalidateNubmer.setClickable(true);
				bt_next.setClickable(true);
				Toast.makeText(baseLoginActivity, "验证码错误", Toast.LENGTH_SHORT)
						.show();
				break;
			}
		};
	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.register_validate_phone_fragment,
				container, false);
		baseLoginActivity = getLoginActivity();
		baseLoginActivity.setLoginTitle("注册");
		initView();
		initSmsSdk();
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		baseLoginActivity.setLoginTitle("注册");
		super.onHiddenChanged(hidden);
	}

	/**
	 * 初始化获取短信验证码
	 */
	private void initSmsSdk() {
		SMSSDK.initSDK(baseLoginActivity, ConstrantValue.MOB_APPKEY,
				ConstrantValue.MOB_APPSECRET);
		EventHandler eventHandler = new EventHandler() {
			@Override
			public void afterEvent(int event, int result, Object data) {
				if (result == SMSSDK.RESULT_COMPLETE
						&& event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					handler.sendEmptyMessage(VALIDATE_SUCCESS);
				} else if (result == SMSSDK.RESULT_ERROR
						&& event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
					handler.sendEmptyMessage(VALIDATE_FAILED);
				}
			}
		};
		SMSSDK.registerEventHandler(eventHandler);
	}

	private void initView() {
		et_phoneNumber = (EditText) view.findViewById(R.id.et_phonenumber);
		et_validateNumber = (EditText) view.findViewById(R.id.et_validatenum);
		bt_getvalidateNubmer = (Button) view
				.findViewById(R.id.bt_getvalidatenum);
		register_loading = (RelativeLayout) view
				.findViewById(R.id.register_loading);
		bt_next = (Button) view.findViewById(R.id.bt_next);
		bt_getvalidateNubmer.setOnClickListener(this);
		bt_next.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_getvalidatenum:// 获取验证码
			phoneNumber = et_phoneNumber.getText().toString().trim();
			if (!TextUtils.isEmpty(phoneNumber)) {
				SMSSDK.getVerificationCode("86", phoneNumber);
				bt_getvalidateNubmer.setClickable(false);
				handler.sendEmptyMessage(BT_CANNOT_CLICK);
			} else {
				Toast.makeText(baseLoginActivity,
						R.string.phoneNumber_not_null, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.bt_next:// 校验验证码
			if (!TextUtils.isEmpty(et_validateNumber.getText().toString())) {
				register_loading.setVisibility(View.VISIBLE);
				bt_next.setClickable(false);
				bt_getvalidateNubmer.setClickable(false);
				SMSSDK.submitVerificationCode("86", phoneNumber,
						et_validateNumber.getText().toString());
			} else {
				Toast.makeText(baseLoginActivity,
						R.string.validateNumber_not_null, Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
	}

	public interface ValidateChild {
		public void changeToRegisterFragment();
	}

	private BaseLoginActivity getLoginActivity() {
		if (getActivity() instanceof ValidateChild) {
			baseLoginActivity = ((BaseLoginActivity) getActivity());
		}
		return baseLoginActivity;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		SMSSDK.unregisterAllEventHandler();
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
