package com.lzstudio.healthy.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.http.json.NewsParser;
import com.lzstudio.healthy.http.utils.CommonUtil;

public class RunDetailActivity extends Activity implements OnClickListener,
		ResponseData {
	private TextView tv_back, tv_title, tv_rundetail_title,
			tv_rundetail_yuedingtime, tv_rundetail_address,
			tv_rundetail_describe, tv_rundetail_fabutime, tv_rundetail_owner;
	private Button bt_rundetail_join;
	private Run run;
	private TextView tv_loading;
	private RelativeLayout rl_rundetail_loading;
	private SharedPreferences sp;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.run_info_details);
		run = (Run) getIntent().getExtras().get("runinfo");
		sp = this.getSharedPreferences("login", Context.MODE_PRIVATE);
		initView();
		initDatas();
	}

	private void initDatas() {
		tv_title.setText("详细信息");
		tv_rundetail_title.setText(run.getTitle());
		tv_rundetail_yuedingtime.setText("约定时间：" + run.getYuedingtime());
		tv_rundetail_address.setText("约定地点：" + run.getAddress());
		tv_rundetail_describe.setText(run.getDescribe());
		tv_rundetail_owner.setText("发布者：" + run.getOwner());
		tv_rundetail_fabutime.setText("发布时间：" + run.getFabutime());
		tv_loading.setText("参与中...");
	}

	private void initView() {
		tv_back = (TextView) this.findViewById(R.id.tv_back);
		tv_back.setOnClickListener(this);
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_rundetail_title = (TextView) this
				.findViewById(R.id.tv_rundetail_title);
		tv_rundetail_fabutime = (TextView) this
				.findViewById(R.id.tv_rundetail_fabutime);
		tv_rundetail_address = (TextView) this
				.findViewById(R.id.tv_rundetail_address);
		tv_rundetail_yuedingtime = (TextView) this
				.findViewById(R.id.tv_rundetail_yuedingtime);
		tv_rundetail_describe = (TextView) this
				.findViewById(R.id.tv_rundetail_describe);
		// 使得textview具有滑动的功能
		tv_rundetail_describe.setMovementMethod(ScrollingMovementMethod
				.getInstance());
		tv_rundetail_owner = (TextView) this
				.findViewById(R.id.tv_rundetail_owner);
		bt_rundetail_join = (Button) this.findViewById(R.id.bt_rundetail_join);
		tv_loading = (TextView) this.findViewById(R.id.tv_loading);
		bt_rundetail_join.setOnClickListener(this);
		if (sp.getString("runid", "").equals(run.getId())) {
			bt_rundetail_join.setText("已参加");
			bt_rundetail_join.setBackgroundColor(0xffababab);
			bt_rundetail_join.setClickable(false);
		}
		rl_rundetail_loading = (RelativeLayout) this
				.findViewById(R.id.rl_rundetail_loading);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_back:
			finish();
			break;
		case R.id.bt_rundetail_join:
			String fabutime = sp.getString("fabutime", "");
			if (fabutime == null || fabutime.equals("")
					|| !fabutime.equals(run.getFabutime())) {
				if (CommonUtil.isNetworkAvailable(this) != 0) {
					rl_rundetail_loading.setVisibility(View.VISIBLE);
					HttpClientUtils.getInstance().joinNow(this, Url.JOIN_RUN,
							sp.getString("phoneNumber", ""), run.getId());
				} else {
					Toast.makeText(this, "请检查您的网络...", Toast.LENGTH_LONG)
							.show();
				}
			} else {
				Toast.makeText(this, "每天只能参加一次哦！", Toast.LENGTH_LONG).show();
			}
			break;
		}
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == 1) {
			String time = NewsParser.getInstance().newRunJsonParser(result);
			if (time == null || time.equals("")) {
				Toast.makeText(this, "服务器出错，请稍后再试...", Toast.LENGTH_SHORT)
						.show();
			} else {
				rl_rundetail_loading.setVisibility(View.GONE);
				Toast.makeText(this, "参加成功", Toast.LENGTH_SHORT).show();
				// 参加成功，在本地保存数据
				sp.edit().putString("fabutime", time)
						.putString("runid", run.getId()).commit();
				bt_rundetail_join.setText("已参加");
				bt_rundetail_join.setBackgroundColor(0xffababab);
				bt_rundetail_join.setClickable(false);
			}
		} else {
			Toast.makeText(this, "网络出现问题了...", Toast.LENGTH_SHORT).show();
		}
	}
}
