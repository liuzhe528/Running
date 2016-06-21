package com.lzstudio.healthy.http.json;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.lzstudio.healthy.bean.NewsDetailModle;

public class NewsDetailParser {
	private NewsDetailParser() {
	}

	static class Instance {
		private static NewsDetailParser instance = new NewsDetailParser();
	}

	public static NewsDetailParser getInstance() {
		return Instance.instance;
	}

	public NewsDetailModle jsonToNewsDetail(String json) {
		NewsDetailModle newsDetail = null;
		if (TextUtils.isEmpty(json)) {
			return newsDetail;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("showapi_res_code");
			if (code == 0) {
				newsDetail = new NewsDetailModle();
				JSONObject item = jsonObject.getJSONObject("showapi_res_body")
						.getJSONObject("item");
				newsDetail.setContent(item.getString("content"));
				newsDetail.setKeyword(item.getString("keywords"));
				newsDetail.setMediaName(item.getString("media_name"));
				newsDetail.setTime(item.getString("ctime"));
				newsDetail.setTitle(item.getString("title"));
			}
			return newsDetail;
		} catch (JSONException e) {
			e.printStackTrace();
			return newsDetail;
		}
	}
}
