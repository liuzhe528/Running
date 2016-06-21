package com.lzstudio.healthy.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.utils.Options;
import com.lzstudio.healthy.view.ciclehead.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.volley.tools.os.tbmy;

public class SettingActivity extends Activity implements OnClickListener,
		ResponseData {
	private TextView tv_setting_back, tv_setting_title, tv_setting_age,
			tv_setting_sex, tv_setting_username, tv_setting_paceCount,
			tv_setting_points, tv_set_sex_man, tv_set_sex_female, tv_loading;
	private Button bt_setting_save, bt_setting_nologin, bt_set_cancle,
			bt_set_sure, bt_set_age_cancle, bt_set_age_sure;
	private RelativeLayout rl_headImg, rl_nickName, rl_sex, rl_age,
			rl_setting_loading;
	private EditText et_set_username, et_set_age;
	private CircleImageView iv_headImg;
	private FrameLayout fl_set_username, fl_set_age, fl_set_sex;
	private SharedPreferences sp;
	private String username, sex;
	private int age, points;
	private long paceCount;
	private static final int PIC_IMG = 100;
	private ImageLoader imageLoader = ImageLoader.getInstance();
	private DisplayImageOptions options;
	private String path;
	private String path_url;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);
		sp = getSharedPreferences("login", Context.MODE_PRIVATE);
		path_url = this.getFilesDir() + "/" + sp.getString("phoneNumber", "")
				+ ".png";
		path = "file://" + path_url;
		initView();
		initData();
	}

	/**
	 * 向服务器获取数据
	 */
	private void initData() {
		username = sp.getString("username", "凉城不暖少年心丶");
		sex = sp.getString("sex", "男");
		age = sp.getInt("age", 20);
		paceCount = sp.getLong("paceCount", 0);
		points = (int) tbmy.getInstance(this).bepg();
		tv_setting_age.setText(age + "岁");
		tv_setting_sex.setText(sex);
		if ("男".equals(sex)) {
			@SuppressWarnings("deprecation")
			Drawable drawable = getResources().getDrawable(
					R.drawable.ic_selected);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_set_sex_man.setCompoundDrawables(null, null, drawable, null);
			tv_set_sex_female.setCompoundDrawables(null, null, null, null);
		} else {
			@SuppressWarnings("deprecation")
			Drawable drawable1 = getResources().getDrawable(
					R.drawable.ic_selected);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			tv_set_sex_female.setCompoundDrawables(null, null, drawable1, null);
			tv_set_sex_man.setCompoundDrawables(null, null, null, null);
		}
		tv_setting_username.setText(username);
		tv_setting_paceCount.setText(paceCount + "步");
		tv_setting_points.setText(points + "分");
		options = Options.getListOptions();
		String str = sp.getString("headImgSource", "");
		if (TextUtils.isEmpty(str)) {
			iv_headImg.setImageResource(R.drawable.ic_login_head);
		} else {
			imageLoader.displayImage(str, iv_headImg, options);
		}
	}

	private void initView() {
		tv_setting_back = (TextView) this.findViewById(R.id.tv_setting_back);
		tv_setting_title = (TextView) this.findViewById(R.id.tv_setting_title);
		tv_setting_age = (TextView) this.findViewById(R.id.tv_setting_age);
		tv_setting_sex = (TextView) this.findViewById(R.id.tv_setting_sex);
		tv_setting_paceCount = (TextView) this
				.findViewById(R.id.tv_setting_paceCount);
		tv_setting_points = (TextView) this
				.findViewById(R.id.tv_setting_points);
		tv_setting_username = (TextView) this
				.findViewById(R.id.tv_setting_username);
		bt_setting_save = (Button) this.findViewById(R.id.bt_setting_save);
		bt_setting_nologin = (Button) this
				.findViewById(R.id.bt_setting_nologin);
		rl_headImg = (RelativeLayout) this.findViewById(R.id.rl_headImg);
		rl_nickName = (RelativeLayout) this.findViewById(R.id.rl_nickName);
		fl_set_username = (FrameLayout) this.findViewById(R.id.fl_set_username);
		fl_set_age = (FrameLayout) this.findViewById(R.id.fl_set_age);
		fl_set_sex = (FrameLayout) this.findViewById(R.id.fl_set_sex);
		rl_sex = (RelativeLayout) this.findViewById(R.id.rl_sex);
		rl_age = (RelativeLayout) this.findViewById(R.id.rl_age);
		iv_headImg = (CircleImageView) this.findViewById(R.id.iv_setting_head);
		et_set_username = (EditText) this.findViewById(R.id.et_set_username);
		bt_set_cancle = (Button) this.findViewById(R.id.bt_set_cancle);
		bt_set_age_cancle = (Button) this.findViewById(R.id.bt_set_age_cancle);
		bt_set_age_sure = (Button) this.findViewById(R.id.bt_set_age_sure);
		bt_set_sure = (Button) this.findViewById(R.id.bt_set_sure);
		tv_set_sex_man = (TextView) this.findViewById(R.id.tv_set_sex_man);
		et_set_age = (EditText) this.findViewById(R.id.et_set_age);
		tv_set_sex_female = (TextView) this
				.findViewById(R.id.tv_set_sex_female);
		rl_setting_loading = (RelativeLayout) this
				.findViewById(R.id.rl_setting_loading);
		tv_loading = (TextView) this.findViewById(R.id.tv_loading);
		tv_loading.setText("正在设置...");
		tv_set_sex_man.setOnClickListener(this);
		tv_set_sex_female.setOnClickListener(this);
		bt_set_cancle.setOnClickListener(this);
		bt_set_sure.setOnClickListener(this);
		bt_set_age_sure.setOnClickListener(this);
		bt_set_age_cancle.setOnClickListener(this);
		tv_setting_back.setOnClickListener(this);
		bt_setting_save.setOnClickListener(this);
		bt_setting_nologin.setOnClickListener(this);
		rl_headImg.setOnClickListener(this);
		rl_sex.setOnClickListener(this);
		rl_age.setOnClickListener(this);
		rl_nickName.setOnClickListener(this);
		tv_setting_title.setText("设置");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_setting_save:// 保存：向服务器发送信息
			rl_setting_loading.setVisibility(View.VISIBLE);
			HttpClientUtils.getInstance().settingJson(this, Url.Setting_Host,
					sp.getString("phoneNumber", ""),
					tv_setting_username.getText().toString(),
					tv_setting_sex.getText().toString(), age, path_url);
			break;
		case R.id.bt_setting_nologin:// 注销登录：删除sp中保存的登录信息
			sp.edit().clear().commit();
			Toast.makeText(this.getApplicationContext(), "注销成功",
					Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.tv_setting_back:
			finish();
			break;
		case R.id.rl_headImg:// 设置头像
			Intent intent = new Intent(Intent.ACTION_PICK,
					Media.EXTERNAL_CONTENT_URI);
			intent.setDataAndType(Media.EXTERNAL_CONTENT_URI, "image/*");
			startActivityForResult(intent, PIC_IMG);
			break;
		case R.id.rl_nickName:// 设置昵称
			fl_set_username.setVisibility(View.VISIBLE);
			break;
		case R.id.rl_sex:// 设置性别
			fl_set_sex.setVisibility(View.VISIBLE);
			break;
		case R.id.rl_age:// 设置年龄
			fl_set_age.setVisibility(View.VISIBLE);
			break;
		case R.id.bt_set_cancle:
			fl_set_username.setVisibility(View.GONE);
			break;
		case R.id.bt_set_sure:
			String str_username = et_set_username.getText().toString().trim();
			if (!TextUtils.isEmpty(str_username)) {
				tv_setting_username.setText(str_username);
				fl_set_username.setVisibility(View.GONE);
			} else {
				Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.bt_set_age_cancle:
			fl_set_age.setVisibility(View.GONE);
			break;
		case R.id.bt_set_age_sure:
			String str_age = et_set_age.getText().toString().trim();
			if (!TextUtils.isEmpty(str_age)) {
				age = Integer.parseInt(str_age);
				if (age >= 0 && age <= 150) {
					tv_setting_age.setText(age + "岁");
					fl_set_age.setVisibility(View.GONE);
				} else {
					Toast.makeText(this, "年龄范围为0~150", Toast.LENGTH_SHORT)
							.show();
				}
			} else {
				Toast.makeText(this, "年龄不能为空", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.tv_set_sex_man:// 替换右边的图片
			@SuppressWarnings("deprecation")
			Drawable drawable = getResources().getDrawable(
					R.drawable.ic_selected);
			drawable.setBounds(0, 0, drawable.getMinimumWidth(),
					drawable.getMinimumHeight());
			tv_set_sex_man.setCompoundDrawables(null, null, drawable, null);
			tv_set_sex_female.setCompoundDrawables(null, null, null, null);
			tv_setting_sex.setText("男");
			fl_set_sex.setVisibility(View.GONE);
			break;
		case R.id.tv_set_sex_female:// 替换右边的图片
			@SuppressWarnings("deprecation")
			Drawable drawable1 = getResources().getDrawable(
					R.drawable.ic_selected);
			drawable1.setBounds(0, 0, drawable1.getMinimumWidth(),
					drawable1.getMinimumHeight());
			tv_set_sex_female.setCompoundDrawables(null, null, drawable1, null);
			tv_set_sex_man.setCompoundDrawables(null, null, null, null);
			tv_setting_sex.setText("女");
			fl_set_sex.setVisibility(View.GONE);
			break;
		}
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

	@Override
	public void getResponseData(int id, String result) {
		rl_setting_loading.setVisibility(View.GONE);
		if (id == 1) {
			JSONObject json;
			try {
				json = new JSONObject(result);
				int result_code = json.getInt("result_code");
				switch (result_code) {
				case 0:
					Toast.makeText(this, "用户昵称已存在", Toast.LENGTH_SHORT).show();
					break;
				case 1:
					Editor edit = sp.edit();
					edit.putString("username", tv_setting_username.getText()
							.toString());
					edit.putInt("age", age);
					edit.putString("headImgSource", path);
					edit.putString("sex", tv_setting_sex.getText().toString());
					edit.commit();
					Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case 2:
					Toast.makeText(this, "服务器正在维护，请改天再试", Toast.LENGTH_SHORT)
							.show();
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(getApplicationContext(), "设置联网失败,请稍候再试",
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PIC_IMG) {
			if (data != null) {
				Intent intent = new Intent(this, SettingHeadActivity.class);
				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor cursor = managedQuery(data.getData(), proj, null, null,
						null);
				int column_index = cursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				cursor.moveToFirst();
				String path = cursor.getString(column_index);
				intent.putExtra("photo_uri", path);
				startActivityForResult(intent, 500);
			}
		}
		if (resultCode == 101) {
			FileInputStream fis = null;
			try {
				fis = this.openFileInput(sp.getString("phoneNumber", "")
						+ ".png");
				Bitmap bitmap = BitmapFactory.decodeStream(fis);
				File file = this.getFilesDir();
				path = "file://" + file.getAbsolutePath() + "/"
						+ sp.getString("phoneNumber", "") + ".png";
				path_url = file.getAbsolutePath() + "/"
						+ sp.getString("phoneNumber", "") + ".png";
				iv_headImg.setImageBitmap(bitmap);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fis != null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
}
