package com.lzstudio.healthy.fragment;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
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
import com.lzstudio.healthy.activity.BaseActivity;
import com.lzstudio.healthy.activity.BaseActivity.ChangeListener;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.view.myprogressview.MyProgressView;
import com.umeng.analytics.MobclickAgent;

public class MainFragment extends Fragment implements OnClickListener,
		ResponseData {
	private MyProgressView progressView;
	private TextView desire_distance;
	private TextView calories, distance;
	private RelativeLayout rl_desire_distance, dialog;
	private EditText et_distance;
	private Button bt_start, bt_stop, bt_cancle, bt_sure;
	private DecimalFormat decimalFormat = new DecimalFormat("#0.00");
	private BaseActivity baseActivity;
	private SharedPreferences sp;
	private float total_distance, disire_distance;
	private boolean isStop = false;

	public interface ChildInter {

	};

	private ChangeListener listener = new ChangeListener() {

		@Override
		public void stepChange(int value) {
			progressView.setCurrentProgress(value);
			isStartClicked = true;
		}

		@Override
		public void distanceChange(float value) {
		}

		@Override
		public void caloriesChange(float value) {
			String str_value = decimalFormat.format(value);
			calories.setText(str_value + " g");
		}

		@Override
		public void timeChanged(long value) {
			progressView.setTime(value);
		}
	};
	/**
	 * 判断是否按了开始
	 */
	private boolean isStartClicked = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.main_fragment, container, false);
		getBaseActivity();
		sp = baseActivity.getSharedPreferences("login", Context.MODE_PRIVATE);
		total_distance = sp.getFloat("distance", 0);
		disire_distance = sp.getFloat("disire_distance", 2000);
		initView(view);
		return view;
	}

	/**
	 * 初始化控件
	 * 
	 * @param view
	 */
	private void initView(View view) {
		progressView = (MyProgressView) view.findViewById(R.id.progressBar);
		desire_distance = (TextView) view.findViewById(R.id.tv_desire_distance);
		calories = (TextView) view.findViewById(R.id.tv_calories);
		rl_desire_distance = (RelativeLayout) view
				.findViewById(R.id.rl_desire_distance);
		dialog = (RelativeLayout) view.findViewById(R.id.dialog);
		et_distance = (EditText) view.findViewById(R.id.et_distance);
		bt_cancle = (Button) view.findViewById(R.id.bt_cancle);
		bt_cancle.setOnClickListener(this);
		bt_sure = (Button) view.findViewById(R.id.bt_sure);
		bt_sure.setOnClickListener(this);
		rl_desire_distance.setOnClickListener(this);
		bt_start = (Button) view.findViewById(R.id.start);
		bt_start.setOnClickListener(this);
		bt_stop = (Button) view.findViewById(R.id.stop);
		bt_stop.setOnClickListener(this);
		distance = (TextView) view.findViewById(R.id.tv_distance);
		String str_v = decimalFormat.format(total_distance);
		distance.setText(str_v + " 千米");
		str_v = decimalFormat.format(disire_distance);
		desire_distance.setText(str_v + " 米");
		progressView.setMaxProgress((int) (disire_distance
				/ sp.getFloat("step_length", 60) * 100));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.start:
			if (!isStartClicked) {
				isStartClicked = true;
				bt_stop.setText("暂停");
				if (baseActivity != null) {
					baseActivity.startStepService();
					baseActivity.bindStepService();
				}
			}
			break;
		case R.id.stop:
			if (isStartClicked) {
				isStartClicked = false;
				if (baseActivity != null) {
					baseActivity.unbindStepService();
					baseActivity.stopStepService();
				}
				bt_stop.setText("提交");
				isStop = true;
			} else {
				String phoneNumber = sp.getString("phoneNumber", "");
				if (!TextUtils.isEmpty(phoneNumber)) {
					if (isStop) {
						isStop = false;
						bt_stop.setText("暂停");
						HttpClientUtils.getInstance().paceCountJson(this,
								Url.Pace_Host, phoneNumber,
								(long) progressView.getCurrentProgress());
					}
				} else {
					Toast.makeText(baseActivity, "您还未登录", Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
		case R.id.bt_cancle:
			dialog.setVisibility(View.GONE);
			et_distance.setText("");
			break;
		case R.id.bt_sure:
			String dis = et_distance.getText().toString();
			if (!TextUtils.isEmpty(dis)) {
				float dis_f = Float.parseFloat(dis);
				if (dis_f > 0 && dis_f <= 100000) {
					dialog.setVisibility(View.GONE);
					desire_distance.setText(decimalFormat.format(dis_f) + " 米");
					sp.edit().putFloat("disire_distance", dis_f).commit();
					progressView.setMaxProgress((int) (dis_f
							/ sp.getFloat("step_length", 60) * 100));
				} else {
					Toast.makeText(baseActivity, "范围为0~100000米",
							Toast.LENGTH_SHORT).show();
				}
			} else {
				Toast.makeText(baseActivity, "距离不能为空", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.rl_desire_distance:
			dialog.setVisibility(View.VISIBLE);
			et_distance.setText(disire_distance + "");
			break;
		}
	}

	private BaseActivity getBaseActivity() {
		if (getActivity() instanceof ChildInter) {
			baseActivity = (BaseActivity) getActivity();
		}
		return baseActivity;
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}

	@Override
	public void onResume() {
		super.onResume();
		if (baseActivity != null) {
			baseActivity.setOnChangeListener(listener);
		}
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
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
					long pace = json.getLong("paceCount");
					sp.edit().putLong("paceCount", pace).commit();
					sp.edit()
							.putFloat(
									"distance",
									pace * sp.getFloat("step_length", 60) / 100
											/ 1000).commit();
					distance.setText(decimalFormat.format(sp.getFloat(
							"distance", 0)) + " 千米");
					Toast.makeText(baseActivity, "提交数据成功", Toast.LENGTH_SHORT)
							.show();
					break;
				case 2:
					Toast.makeText(baseActivity, "服务器正在维护，请改天再试",
							Toast.LENGTH_SHORT).show();
					break;
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		} else {
			Toast.makeText(baseActivity, "提交步数失败，请稍后再试", Toast.LENGTH_SHORT)
					.show();
		}
	}
}
