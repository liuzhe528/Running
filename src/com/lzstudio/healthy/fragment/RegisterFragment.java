package com.lzstudio.healthy.fragment;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
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

public class RegisterFragment extends Fragment implements OnClickListener,
		ResponseData {
	private View view;
	private BaseLoginActivity baseLoginActivity;
	private EditText et_password;
	private EditText et_confirm_password, register_username;
	private RelativeLayout register_loading;
	private Button register;
	private String regex = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{8,16}$";
	private String username_regex = "^[a-zA-Z0-9_\u4e00-\u9fa5]+$";
	private String phoneNumber;
	private TextView tv_loading;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.set_password_fragment, container,
				false);
		baseLoginActivity = getBaseLoginActivity();
		if (baseLoginActivity != null) {
			baseLoginActivity.setLoginTitle("设置密码");
			baseLoginActivity.setBackGone();
		}
		phoneNumber = baseLoginActivity.getSharedPreferences("login",
				Context.MODE_PRIVATE).getString("phoneNumber", "");
		initView();
		return view;
	}

	private void initView() {
		et_password = (EditText) view.findViewById(R.id.register_password);
		et_confirm_password = (EditText) view
				.findViewById(R.id.confirm_password);
		register_username = (EditText) view
				.findViewById(R.id.register_username);
		register_loading = (RelativeLayout) view
				.findViewById(R.id.rl_register_loading);
		register = (Button) view.findViewById(R.id.bt_register_password);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
		tv_loading.setText("正在注册...");
		register.setOnClickListener(this);
	}

	public interface RegisterChild {
	}

	private BaseLoginActivity getBaseLoginActivity() {
		if (getActivity() instanceof RegisterChild) {
			baseLoginActivity = (BaseLoginActivity) getActivity();
		}
		return baseLoginActivity;
	}

	@Override
	public void onClick(View v) {
		String password = et_password.getText().toString().trim();
		String confirmPassword = et_confirm_password.getText().toString()
				.trim();
		String username = register_username.getText().toString().trim();
		String registerTime = System.currentTimeMillis() + "";
		if (!username.matches(username_regex)) {
			Toast.makeText(baseLoginActivity, "用户昵称不合法", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (!password.equals(confirmPassword)) {
			Toast.makeText(baseLoginActivity, "两次输入的密码不一致", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		if (!password.matches(regex)) {
			Toast.makeText(baseLoginActivity, "密码不符合规则", Toast.LENGTH_SHORT)
					.show();
			return;
		}
		register.setClickable(false);
		try {
			password = DES.encryptDES(password, ConstrantValue.DES_KEY);
			password = DES.encryptDES(password, ConstrantValue.DES_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		register_loading.setVisibility(View.VISIBLE);
		HttpClientUtils.getInstance().registerJson(this, Url.REGISTER_HOST,
				username, password, phoneNumber, registerTime);
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

	@Override
	public void getResponseData(int id, String result) {
		register.setClickable(true);
		register_loading.setVisibility(View.GONE);
		if (id == 1) {
			try {
				JSONObject json = new JSONObject(result);
				int result_code = json.getInt("result_code");
				switch (result_code) {
				case 0:
					Toast.makeText(baseLoginActivity, "该手机号已注册",
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Toast.makeText(baseLoginActivity, "注册成功",
							Toast.LENGTH_SHORT).show();
					baseLoginActivity.changeToLoginFragment();
					break;
				case 2:
					Toast.makeText(baseLoginActivity, "用户昵称已存在",
							Toast.LENGTH_SHORT).show();
					break;
				case 3:
					Toast.makeText(baseLoginActivity, "服务器正在维护,请改天再试",
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(baseLoginActivity, "注册联网失败,请稍候再试",
					Toast.LENGTH_SHORT).show();
		}
	}
}
