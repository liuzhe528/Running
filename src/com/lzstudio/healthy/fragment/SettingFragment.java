package com.lzstudio.healthy.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseActivity;
import com.lzstudio.healthy.pedometer.StepService;
import com.umeng.analytics.MobclickAgent;

public class SettingFragment extends Fragment {
	private View view;
	private EditText et_weight, et_length;
	private Button bt_save;
	private Spinner spinner;
	private List<String> items = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private SharedPreferences sp;
	private float str_length;
	private float str_weight;
	private int itemSelected;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.setting_fragment, container, false);
		sp = getActivity()
				.getSharedPreferences("setting", Context.MODE_PRIVATE);
		str_length = sp.getFloat("step_length", 60);
		str_weight = sp.getFloat("body_weight", 50);
		itemSelected = sp.getInt("selected", 1);
		initView();
		et_length.setText(str_length + "");
		et_weight.setText(str_weight + "");
		spinner.setSelection(itemSelected);
		return view;
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		str_length = sp.getFloat("step_length", 60);
		str_weight = sp.getFloat("body_weight", 50);
		itemSelected = sp.getInt("selected", 7);
		et_length.setText(str_length + "");
		et_weight.setText(str_weight + "");
		spinner.setSelection(itemSelected);
	}

	private void initView() {
		et_length = (EditText) view.findViewById(R.id.setting_et_length);
		et_weight = (EditText) view.findViewById(R.id.setting_et_weight);
		spinner = (Spinner) view.findViewById(R.id.setting_sensitivity);
		bt_save = (Button) view.findViewById(R.id.setting_save);
		items.add("特别高");
		items.add("非常高");
		items.add("高");
		items.add("较高");
		items.add("中等");
		items.add("较低");
		items.add("低");
		items.add("非常低");
		items.add("特别低");
		adapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, items);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		bt_save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String weight = et_weight.getText().toString();
				String length = et_length.getText().toString();
				if (TextUtils.isEmpty(weight) || TextUtils.isEmpty(length)) {
					return;
				}
				float len = Float.parseFloat(length);
				float weight_int = Float.parseFloat(weight);
				if (weight_int >= 20 && weight_int <= 200) {
					if (len >= 50 && len <= 110) {
						itemSelected = spinner.getSelectedItemPosition();
						sp.edit().putFloat("step_length", len).commit();
						sp.edit().putFloat("body_weight", weight_int).commit();
						if (0 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 1.9753f).commit();
						} else if (1 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 2.9630f).commit();
						} else if (2 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 4.4444f).commit();
						} else if (3 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 6.6667f).commit();
						} else if (4 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 10f).commit();
						} else if (5 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 15f).commit();
						} else if (6 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 22.5f).commit();
						} else if (7 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 33.75f).commit();
						} else if (8 == spinner.getSelectedItemPosition()) {
							sp.edit().putFloat("sensitivity", 50.625f).commit();
						}
						sp.edit().putInt("selected", itemSelected).commit();
						StepService service = ((BaseActivity) getActivity())
								.getStepService();
						if (service != null) {
							service.reloadSettings();
						}
						Toast.makeText(getActivity(), "保存成功",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(getActivity(), "步长范围为50~110,请重新设置",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity(), "体重范围为20~200,请重新设置",
							Toast.LENGTH_SHORT).show();
				}
			}
		});
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
