package com.lzstudio.healthy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lzstudio.healthy.R;

public class AboutUsActivity extends Activity {
	private TextView tv_back;
	private TextView tv_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_us_activity);
		tv_back = (TextView) findViewById(R.id.tv_back);
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("使用说明");
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
}
