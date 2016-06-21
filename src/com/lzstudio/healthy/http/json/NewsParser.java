package com.lzstudio.healthy.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.lzstudio.healthy.bean.NewsModle;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.bean.User;

public class NewsParser {
	private NewsParser() {
	}

	static class Seed {
		private static NewsParser instance = new NewsParser();
	}

	public static NewsParser getInstance() {
		return Seed.instance;
	}

	/**
	 * 发布Run信息返回的结果，以及立即参加都可以调用此方法
	 * 
	 * @param json
	 * @return
	 */
	public String newRunJsonParser(String json) {
		String fabutime = "";
		if (json == null || json.equals("")) {
			return fabutime;
		}
		JSONObject jsonObj;
		try {
			jsonObj = new JSONObject(json);
			int code = jsonObj.getInt("result_code");
			if (code == 1) {
				fabutime = jsonObj.getString("fabutime");
			}
			return fabutime;
		} catch (JSONException e) {
			e.printStackTrace();
			return fabutime;
		}
	}

	/**
	 * 参加的Run信息解析
	 * 
	 * @param json
	 * @return
	 */
	public List<Object> joinedJsonToBean(String json) {
		List<Object> list = new ArrayList<Object>();
		if (json == null || json.equals("")) {
			return list;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("result_code");
			if (code == 1) {
				JSONObject runObj = jsonObject.getJSONObject("run");
				Run run = new Run();
				run.setTitle(runObj.getString("title"));
				run.setYuedingtime(runObj.getString("yuedingtime"));
				run.setAddress(runObj.getString("address"));
				run.setFabutime(runObj.getString("fabutime"));
				run.setOwner(runObj.getString("owner"));
				run.setDescribe(runObj.getString("describe"));
				list.add(run);
				JSONArray array = jsonObject.getJSONArray("body");
				List<User> userList = new ArrayList<User>();
				for (int i = 0; i < array.length(); i++) {
					User user = new User();
					user.setHeadImg(array.getJSONObject(i).getString(
							"headImgSource"));
					user.setUsername(array.getJSONObject(i).getString(
							"username"));
					user.setPhoneNumber(array.getJSONObject(i).getString(
							"phoneNumber"));
					userList.add(user);
				}
				list.add(userList);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
			return list;
		}
	}

	/**
	 * 解析正在进行的Run信息
	 * 
	 * @param json
	 * @return
	 */
	public List<Run> ongoingJsonToBean(String json) {
		List<Run> runList = new ArrayList<Run>();
		if (json == null || json.equals("")) {
			return runList;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("result_code");
			if (code == 1) {
				JSONArray array = jsonObject.getJSONArray("run");
				for (int i = 0; i < array.length(); i++) {
					Run run = new Run();
					run.setId(array.getJSONObject(i).getString("id"));
					run.setTitle(array.getJSONObject(i).getString("title"));
					run.setYuedingtime(array.getJSONObject(i).getString(
							"yuedingtime"));
					run.setAddress(array.getJSONObject(i).getString("address"));
					run.setFabutime(array.getJSONObject(i)
							.getString("fabutime"));
					run.setOwner(array.getJSONObject(i).getString("owner"));
					run.setDescribe(array.getJSONObject(i)
							.getString("describe"));
					runList.add(run);
				}
			}
			return runList;
		} catch (JSONException e) {
			e.printStackTrace();
			return runList;
		}
	}

	public List<NewsModle> jsonToNewsModle(String json) {
		List<NewsModle> newsList = new ArrayList<NewsModle>();
		if (json == null || json.equals("")) {
			return newsList;
		}
		try {
			JSONObject jsonObject = new JSONObject(json);
			int code = jsonObject.getInt("showapi_res_code");
			if (code == 0) {
				JSONObject pageBean = jsonObject.getJSONObject(
						"showapi_res_body").getJSONObject("pagebean");
				JSONArray array = pageBean.getJSONArray("contentlist");
				for (int i = 0; i < array.length(); i++) {
					NewsModle news = new NewsModle();
					news.setId(array.getJSONObject(i).getString("id"));
					news.setTime(array.getJSONObject(i).getString("ctime"));
					news.setIntro(array.getJSONObject(i).getString("intro"));
					news.setTitle(array.getJSONObject(i).getString("title"));
					newsList.add(news);
				}
			}
			return newsList;
		} catch (JSONException e) {
			e.printStackTrace();
			return newsList;
		}
	}

	public List<User> jsonToUsersModle(String result) {
		List<User> users = new ArrayList<User>();
		if (result == null || result.equals("")) {
			return users;
		}
		try {
			JSONObject jsonObject = new JSONObject(result);
			int code = jsonObject.getInt("result_code");
			if (code == 1) {
				JSONArray array = jsonObject.getJSONArray("rank");
				for (int i = 0; i < array.length(); i++) {
					User user = new User();
					JSONObject json = array.getJSONObject(i);
					user.setUsername(json.getString("username"));
					user.setPaceCount(json.getLong("paceCount"));
					user.setHeadImg(json.getString("headImgSource"));
					user.setAge(json.getInt("age"));
					user.setSex(json.getString("sex"));
					users.add(user);
				}
			}
			return users;
		} catch (JSONException e) {
			e.printStackTrace();
			return users;
		}
	}
}
