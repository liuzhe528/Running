package com.lzstudio.healthy.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.lzstudio.healthy.bean.WeatherModle;
import com.lzstudio.healthy.utils.TimeUtils;

public class WeatherListJson extends JsonPacket {
	public List<WeatherModle> weatherListModles = new ArrayList<WeatherModle>();
	public static WeatherListJson weatherListJson;

	public WeatherListJson(Context context) {
		super(context);
	}

	public static WeatherListJson instance(Context context) {
		if (weatherListJson == null) {
			weatherListJson = new WeatherListJson(context);
		}
		return weatherListJson;
	}

	public List<WeatherModle> readJsonWeatherListModles(String result) {
		weatherListModles.clear();
		try {
			if (result == null || result.equals("")) {
				return null;
			}
			WeatherModle weatherModle = null;
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONObject("data")
					.getJSONArray("forecast");
			for (int i = 0; i < jsonArray.length(); i++) {
				weatherModle = readJsonWeatherModles(jsonArray.getJSONObject(i));
				weatherListModles.add(weatherModle);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.gc();
		}
		return weatherListModles;
	}

	private WeatherModle readJsonWeatherModles(JSONObject jsonObject)
			throws Exception {
		WeatherModle weatherModle = null;
		String temperature = "";
		String weather = "";
		String wind = "";
		String date = "";
		String week = "";
		temperature = getString("high", jsonObject) + " "
				+ getString("low", jsonObject);
		weather = getString("type", jsonObject);
		wind = getString("fengxiang", jsonObject);
		date = getString("date", jsonObject);
		weatherModle = new WeatherModle();
		date = TimeUtils.getCurrentTime() + date;
		weatherModle.setDate(date);
		weatherModle.setTemperature(temperature);
		weatherModle.setWeather(weather);
		week = "星" + date.split("日星")[1];
		weatherModle.setWeek(week);
		weatherModle.setWind(wind);

		return weatherModle;
	}
}
