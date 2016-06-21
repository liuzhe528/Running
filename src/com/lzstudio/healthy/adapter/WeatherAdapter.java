package com.lzstudio.healthy.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.WeatherModle;

public class WeatherAdapter extends BaseAdapter {

	public List<WeatherModle> lists = new ArrayList<WeatherModle>();

	private Context context;
	private WeatherModle weatherModle;

	public WeatherAdapter(Context context) {
		this.context = context;
	}

	public void appendList(List<WeatherModle> list) {
		if (!lists.containsAll(list) && list != null && list.size() > 0) {
			lists.addAll(list);
		}
		notifyDataSetChanged();
	}

	public void clear() {
		lists.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return lists.size();
	}

	@Override
	public Object getItem(int position) {
		return lists.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		weatherModle = lists.get(position);
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.item_weather, null);
			holder.mTemp = (TextView) convertView
					.findViewById(R.id.temperature);
			holder.iv_weather = (ImageView) convertView
					.findViewById(R.id.weahter_image);
			holder.mWeather = (TextView) convertView.findViewById(R.id.weather);
			holder.mWeek = (TextView) convertView.findViewById(R.id.week);
			holder.mWind = (TextView) convertView.findViewById(R.id.wind);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.mWeek.setText(weatherModle.getWeek());
		holder.mTemp.setText(weatherModle.getTemperature().replace("低温", "~")
				.split("高温")[1]);
		holder.mWeather.setText(weatherModle.getWeather());
		holder.mWind.setText(weatherModle.getWind());
		setWeatherImage(holder.iv_weather, weatherModle.getWeather());
		return convertView;
	}

	public void setWeatherImage(ImageView mWeatherImage, String weather) {
		if (weather.equals("多云") || weather.equals("多云转阴")
				|| weather.equals("多云转晴")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_duoyun);
		} else if (weather.equals("中雨") || weather.equals("中到大雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhongyu);
		} else if (weather.equals("雷阵雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_leizhenyu);
		} else if (weather.equals("阵雨") || weather.equals("阵雨转多云")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhenyu);
		} else if (weather.equals("暴雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_baoxue);
		} else if (weather.equals("暴雨")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_baoyu);
		} else if (weather.equals("大暴雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_dabaoyu);
		} else if (weather.equals("大雪")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_daxue);
		} else if (weather.equals("大雨") || weather.equals("大雨转中雨")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_dayu);
		} else if (weather.equals("雷阵雨冰雹")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_leizhenyubingbao);
		} else if (weather.equals("晴")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_qing);
		} else if (weather.equals("沙尘暴")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_shachenbao);
		} else if (weather.equals("特大暴雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_tedabaoyu);
		} else if (weather.equals("雾") || weather.equals("雾霾")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_wu);
		} else if (weather.equals("小雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_xiaoxue);
		} else if (weather.equals("小雨")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_xiaoyu);
		} else if (weather.equals("阴")) {
			mWeatherImage.setImageResource(R.drawable.biz_plugin_weather_yin);
		} else if (weather.equals("雨夹雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_yujiaxue);
		} else if (weather.equals("阵雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhenxue);
		} else if (weather.equals("中雪")) {
			mWeatherImage
					.setImageResource(R.drawable.biz_plugin_weather_zhongxue);
		}
	}

	class ViewHolder {
		TextView mWeek;
		TextView mTemp;
		TextView mWeather;
		TextView mWind;
		ImageView iv_weather;
	}
}
