package com.lzstudio.healthy.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseLoginActivity;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.utils.ConstrantValue;
import com.lzstudio.healthy.utils.des.DES;
import com.umeng.analytics.MobclickAgent;

public class LoginFragment extends Fragment implements OnClickListener,
		ResponseData {
	private EditText et_username;
	private EditText et_password;
	private CheckBox cb_savePassword;
	private Button bt_login;
	private Button bt_register;
	private SharedPreferences sp;
	private BaseLoginActivity baseLoginActivity;
	private View view;
	private ChangeFragmentListener listener;
	private RelativeLayout rl_login_loading;
	private TextView tv_loading;
	private String phoneNumber;
	private String encodedPassword;

	public interface ChangeFragmentListener {
		public void changeToValidateFragment();
	}

	public void setChangeFragmentListener(ChangeFragmentListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.login_fragment, container, false);
		baseLoginActivity = getLoginActivity();
		baseLoginActivity.setLoginTitle("登录");
		initView();
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		baseLoginActivity.setLoginTitle("登录");
		super.onHiddenChanged(hidden);
	}

	private BaseLoginActivity getLoginActivity() {
		if (getActivity() instanceof ChangeFragmentListener) {
			baseLoginActivity = ((BaseLoginActivity) getActivity());
		}
		return baseLoginActivity;
	}

	private void initView() {
		et_username = (EditText) view.findViewById(R.id.et_username);
		et_password = (EditText) view.findViewById(R.id.et_password);
		cb_savePassword = (CheckBox) view.findViewById(R.id.cb_savePassword);
		bt_login = (Button) view.findViewById(R.id.bt_login);
		bt_register = (Button) view.findViewById(R.id.bt_register);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
		rl_login_loading = (RelativeLayout) view
				.findViewById(R.id.rl_login_loading);
		tv_loading.setText("正在登陆...");
		bt_register.setOnClickListener(this);
		bt_login.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_login:
			// 1，获取输入框的内容进行本地校验用户名密码不为空
			phoneNumber = et_username.getText().toString().trim();
			String password = et_password.getText().toString().trim();
			if (TextUtils.isEmpty(phoneNumber)) {
				Toast.makeText(baseLoginActivity, R.string.account_cannot_null,
						Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(password)) {
				Toast.makeText(baseLoginActivity,
						R.string.password_cannot_null, Toast.LENGTH_SHORT)
						.show();
			} else {
				try {
					// 2，对密码进行加密操作
					encodedPassword = DES.encryptDES(password,
							ConstrantValue.DES_KEY);
					encodedPassword = DES.encryptDES(encodedPassword,
							ConstrantValue.DES_KEY);
					// 3.向服务器提交数据
					rl_login_loading.setVisibility(View.VISIBLE);
					HttpClientUtils.getInstance().loginJson(this,
							Url.LOGIN_HOST, phoneNumber, encodedPassword);
				} catch (Exception e) {
					Toast.makeText(baseLoginActivity,
							R.string.password_encode_failed, Toast.LENGTH_SHORT)
							.show();
					rl_login_loading.setVisibility(View.GONE);
					e.printStackTrace();
				}
			}
			break;
		case R.id.bt_register:
			// 通知activity更改fragment
			listener.changeToValidateFragment();
			break;
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
		sp = baseLoginActivity.getSharedPreferences("login",
				Context.MODE_PRIVATE);
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
	}

	@Override
	public void getResponseData(int id, String result) {
		rl_login_loading.setVisibility(View.GONE);
		if (id == 1) {
			JSONObject json;
			try {
				json = new JSONObject(result);
				int result_code = json.getInt("result_code");
				switch (result_code) {
				case 0:
					Toast.makeText(baseLoginActivity, "该手机号尚未注册",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					sp.edit()
							.putBoolean("isSavePasswordChecked",
									cb_savePassword.isChecked()).commit();
					if (cb_savePassword.isChecked()) {
						sp.edit().putString("password", encodedPassword)
								.commit();
					}
					JSONObject body = json.getJSONObject("body");
					String username = body.getString("username");
					String sex = body.getString("sex");
					int age = body.getInt("age");
					String headImagSource = body.getString("headImgSource");
					int points = body.getInt("points");
					long paceCount = body.getLong("paceCount");
					Toast.makeText(getActivity().getApplicationContext(),
							"登陆成功", Toast.LENGTH_SHORT).show();
					Editor et = sp.edit();
					et.putString("username", username);
					et.putString("sex", sex);
					et.putString("phoneNumber", phoneNumber);
					et.putInt("age", age);
					et.putString("headImgSource", headImagSource);
					et.putLong("paceCount", paceCount);
					et.putInt("points", points);
					et.commit();
					baseLoginActivity.finish();
					break;
				case 2:
					Toast.makeText(baseLoginActivity, "密码错误",
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(baseLoginActivity, "服务器正在维护，请改天再试",
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(baseLoginActivity, "登录联网失败,请稍候再试",
					Toast.LENGTH_SHORT).show();
		}
	}
}
