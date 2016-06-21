package com.lzstudio.healthy.fragment;

import java.text.DecimalFormat;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.activity.BaseActivity;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.Url;
import com.lzstudio.healthy.view.shop.GuaGuaView;
import com.umeng.analytics.MobclickAgent;
import com.volley.tools.os.tbmy;

public class ShopFragment extends Fragment implements OnClickListener,
		ResponseData {
	private View view;
	private Button bt_getCard, bt_lingjiang, bt_gethuafei;
	private TextView tv_point, tv_huafei;
	private GuaGuaView guagua;
	private RelativeLayout rl_shop_loading, rl_shop_lingqu, rl_guagua;
	private TextView tv_loading, tv_shop_loading;
	private float huafei = 0;
	private int luc = 0;
	private String str[] = { "谢谢参与", "一等奖", "二等奖", "三等奖" };
	private String jiangli[] = { "恭喜获得0.4元话费", "恭喜获得0.3元话费", "恭喜获得0.1元话费" };
	private float pointsBalance;
	private DecimalFormat decimalFormat = new DecimalFormat("#0.0");
	private SharedPreferences sp;
	private float hua = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.shop_fragment, container, false);
		sp = getActivity().getSharedPreferences("login", Context.MODE_PRIVATE);
		initView();
		return view;
	}

	private void initView() {
		bt_getCard = (Button) view.findViewById(R.id.bt_getCard);
		bt_lingjiang = (Button) view.findViewById(R.id.bt_lingjiang);
		bt_gethuafei = (Button) view.findViewById(R.id.bt_gethuafei);
		tv_point = (TextView) view.findViewById(R.id.shop_point);
		tv_huafei = (TextView) view.findViewById(R.id.shop_huafei);
		rl_guagua = (RelativeLayout) view.findViewById(R.id.shop_view);
		tv_loading = (TextView) view.findViewById(R.id.tv_loading);
		tv_shop_loading = (TextView) view.findViewById(R.id.tv_shop_loading);
		rl_shop_loading = (RelativeLayout) view
				.findViewById(R.id.rl_shop_loading);
		rl_shop_lingqu = (RelativeLayout) view
				.findViewById(R.id.rl_shop_lingqu);
		bt_getCard.setOnClickListener(this);
		bt_lingjiang.setOnClickListener(this);
		bt_gethuafei.setOnClickListener(this);
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		if (!hidden) {
			pointsBalance = tbmy.getInstance(getActivity()).bepg();
			tv_point.setText("积分:" + pointsBalance + "分");
			tv_shop_loading.setText("正在加载...");
			rl_shop_loading.setVisibility(View.VISIBLE);
			HttpClientUtils.getInstance().chaxunhuafeiJson(this,
					Url.ChaXunHuaFei, sp.getString("phoneNumber", ""));
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart("MainScreen"); // 统计页面
		pointsBalance = tbmy.getInstance(getActivity()).bepg();
		tv_point.setText("积分:" + pointsBalance + "分");
		tv_shop_loading.setText("正在加载...");
		rl_shop_loading.setVisibility(View.VISIBLE);
		HttpClientUtils.getInstance().chaxunhuafeiJson(this, Url.ChaXunHuaFei,
				sp.getString("phoneNumber", ""));
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.bt_getCard:
			tv_loading.setText("正在获取...");
			rl_shop_lingqu.setVisibility(View.VISIBLE);
			HttpClientUtils.getInstance().pointJson(this, Url.Point_Host,
					sp.getString("phoneNumber", ""),
					System.currentTimeMillis(), 50, 3);
			break;
		case R.id.bt_lingjiang:
			if (luc != 0) {
				switch (luc) {
				case 1:
					hua = 0.4f;
					break;
				case 2:
					hua = 0.3f;
					break;
				case 3:
					hua = 0.1f;
					break;
				}
				tv_loading.setText("正在领取...");
				rl_shop_lingqu.setVisibility(View.VISIBLE);
				HttpClientUtils.getInstance().huafeiJson(this, Url.Huafei_Host,
						sp.getString("phoneNumber", ""),
						System.currentTimeMillis(), decimalFormat.format(hua));
			} else {
				Toast.makeText(getActivity(), "没有奖品可以领取哦", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		case R.id.bt_gethuafei:
			if (huafei >= 10) {
				tv_loading.setText("正在兑换...");
				rl_shop_lingqu.setVisibility(View.VISIBLE);
				HttpClientUtils.getInstance().duihuanJson(this,
						Url.DuiHuan_Host, sp.getString("phoneNumber", ""),
						System.currentTimeMillis(), "hahahahehehe");
			} else {
				Toast.makeText(getActivity(), "话费不足10元不能兑换", Toast.LENGTH_SHORT)
						.show();
			}
			break;
		}
	}

	private int genarateLuckyNumber() {
		int num = (int) (Math.random() * 100);
		int lucky = 0;
		if (num >= 0 && num < 69) {
			lucky = 0;// 谢谢参与
		} else if (num >= 70 && num < 85) {
			lucky = 3;// 三等奖
		} else if (num >= 85 && num < 95) {
			lucky = 2;// 二等奖
		} else if (num >= 95 && num < 100) {
			lucky = 1;// 一等奖
		}
		return lucky;
	}

	@Override
	public void getResponseData(int id, String result) {
		rl_shop_loading.setVisibility(View.GONE);
		rl_shop_lingqu.setVisibility(View.GONE);
		switch (id) {
		case -2:
			Toast.makeText(getActivity(), "加密过程失败", Toast.LENGTH_SHORT).show();
			break;
		case -1:// 获取失败
			Toast.makeText(getActivity(), "登录联网失败,请稍候再试", Toast.LENGTH_SHORT)
					.show();
			break;
		case 1:// 改变话费
			JSONObject json5;
			try {
				json5 = new JSONObject(result);
				int result_code = json5.getInt("result_code");
				if (result_code == 1) {
					huafei = huafei + hua;
					tv_huafei.setText("话费:" + decimalFormat.format(huafei)
							+ "元");
					Toast.makeText(getActivity(), jiangli[luc - 1],
							Toast.LENGTH_SHORT).show();
					rl_guagua.removeAllViews();
					luc = 0;
					hua = 0;
				} else {
					Toast.makeText(getActivity(), "获取失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 2:// 改变积分
			JSONObject json1;
			try {
				json1 = new JSONObject(result);
				int result_code = json1.getInt("result_code");
				if (result_code == 1) {
					boolean isSuccess = tbmy.getInstance(getActivity())
							.bfrg(50);
					if (isSuccess) {
						pointsBalance = tbmy.getInstance(getActivity()).bepg();
						tv_point.setText("积分:" + pointsBalance + "分");
						luc = genarateLuckyNumber();
						rl_guagua.removeAllViews();
						guagua = new GuaGuaView(getActivity());
						((BaseActivity) getActivity()).getResideMenu()
								.addIgnoredView(guagua);
						if (luc == 0) {
							guagua.setmText(str[luc]);
						} else {
							guagua.setmText("恭喜您获得：" + str[luc]);
						}
						rl_guagua.addView(guagua, new LayoutParams(
								LayoutParams.MATCH_PARENT,
								LayoutParams.MATCH_PARENT));
					} else {
						Toast.makeText(getActivity(), "积分不足",
								Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(getActivity(), "积分不足", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 3:// 兑换话费
			JSONObject json3;
			try {
				json3 = new JSONObject(result);
				int result_code = json3.getInt("result_code");
				if (result_code == 1) {
					Toast.makeText(getActivity(), "兑换成功", Toast.LENGTH_SHORT)
							.show();
					huafei = huafei - 10;
					tv_huafei.setText("话费:" + decimalFormat.format(huafei)
							+ "元");
				} else if (result_code == 0) {
					Toast.makeText(getActivity(), "话费不足10元", Toast.LENGTH_SHORT)
							.show();
				} else {
					Toast.makeText(getActivity(), "兑换失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		case 5:// 查询话费
			JSONObject json;
			try {
				json = new JSONObject(result);
				int result_code = json.getInt("result_code");
				if (result_code == 1) {
					double hf = json.getDouble("huafei");
					huafei = (float) hf;
					tv_huafei.setText("话费:" + decimalFormat.format(hf) + "元");
				} else {
					Toast.makeText(getActivity(), "查询话费失败", Toast.LENGTH_SHORT)
							.show();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			break;
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd("MainScreen");
	}
}
