package com.lzstudio.healthy.activity;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.view.cutphoto.ClipImageLayout;

public class SettingHeadActivity extends Activity implements OnClickListener {
	private TextView tv_title, tv_back;
	private Button bt_sure;
	private ClipImageLayout iv_layout;
	private String photoPath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		photoPath = getIntent().getExtras().getString("photo_uri");
		setContentView(R.layout.set_head_img);
		initView();
	}

	private void initView() {
		tv_back = (TextView) this.findViewById(R.id.tv_setting_back);
		tv_title = (TextView) this.findViewById(R.id.tv_setting_title);
		bt_sure = (Button) this.findViewById(R.id.bt_setting_save);
		iv_layout = (ClipImageLayout) this.findViewById(R.id.set_head_layout);
		tv_title.setText("设置头像");
		bt_sure.setText("确定");
		tv_back.setOnClickListener(this);
		bt_sure.setOnClickListener(this);
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
		int w = metric.widthPixels; // 屏幕宽度（像素）
		int h = metric.heightPixels - 60;
		BitmapFactory.Options opts = new Options();
		opts.inJustDecodeBounds = true;// 只读边，不读内容
		Bitmap bitmap = BitmapFactory.decodeFile(photoPath, opts);
		int width = opts.outWidth;
		int height = opts.outHeight;
		int scale = 1;
		if (w > h && width > w) {
			scale = width / w;
		} else if (h > w && height > h) {
			scale = height / h;
		}
		if (scale <= 0) {
			scale = 1;
		}
		opts.inSampleSize = scale + 1;
		opts.inJustDecodeBounds = false;
		bitmap = BitmapFactory.decodeFile(photoPath, opts);
		iv_layout.setImageViewBg(bitmap);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_setting_back:
			finish();
			break;
		case R.id.bt_setting_save:
			Bitmap bitmap = iv_layout.clip();
			FileOutputStream fos = null;
			try {
				fos = this.openFileOutput(
						getSharedPreferences("login", Context.MODE_PRIVATE)
								.getString("phoneNumber", "") + ".png",
						Context.MODE_PRIVATE);
				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
				setResult(101);
				finish();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (fos != null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			break;
		}
	}
}
