package com.lzstudio.healthy.activity;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.fragment.LoginFragment;
import com.lzstudio.healthy.fragment.LoginFragment.ChangeFragmentListener;
import com.lzstudio.healthy.fragment.RegisterFragment;
import com.lzstudio.healthy.fragment.RegisterFragment.RegisterChild;
import com.lzstudio.healthy.fragment.ValidateFragment;
import com.lzstudio.healthy.fragment.ValidateFragment.ValidateChild;
import com.umeng.analytics.MobclickAgent;

public class BaseLoginActivity extends Activity implements
		ChangeFragmentListener, ValidateChild, RegisterChild, OnClickListener {
	private TextView tv_title, tv_back;
	private Fragment content, loginFragment, validateFragment,
			registerFragment;
	private FragmentManager fragmentManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_base_activity);
		initView();
		loginFragment = new LoginFragment();
		validateFragment = new ValidateFragment();
		registerFragment = new RegisterFragment();
		((LoginFragment) loginFragment).setChangeFragmentListener(this);
		((ValidateFragment) validateFragment).setValidateChildListener(this);
		fragmentManager = this.getFragmentManager();
		fragmentManager.beginTransaction()
				.add(R.id.layout_content_login, loginFragment).commit();
		content = loginFragment;
	}

	private void initView() {
		tv_back = (TextView) this.findViewById(R.id.tv_back);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_back.setOnClickListener(this);
	}

	public void setLoginTitle(String title) {
		tv_title.setText(title);
	}

	public void setBackGone() {
		tv_back.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			changeFragment();
			break;
		}
	}

	/**
	 * 根据当前显示的内容改变fragment
	 */
	private void changeFragment() {
		if (content == validateFragment) {
			switchContent(content, loginFragment);
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			changeFragment();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void changeToValidateFragment() {
		switchContent(content, validateFragment);
	}

	public void changeToLoginFragment() {
		switchContent(content, loginFragment);
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
				transaction.hide(from).add(R.id.layout_content_login, to)
						.commit(); // 隐藏当前的fragment，add下一个到Activity中
			} else {
				transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
			}
		}
	}

	@Override
	public void changeToRegisterFragment() {
		switchContent(content, registerFragment);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
