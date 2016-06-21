package com.lzstudio.healthy.http.json;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.lzstudio.healthy.bean.Colums;

public class ColumsParser {
	public static List<Colums> json2Colums(String jsonString) {
		List<Colums> colums = new ArrayList<Colums>();
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			int resultCode = jsonObject.getInt("showapi_res_code");
			if (resultCode == 0) {
				JSONObject obj = jsonObject.getJSONObject("showapi_res_body");
				JSONArray array = obj.getJSONArray("list");
				for (int i = 0; i < array.length(); i++) {
					Colums colum = new Colums();
					colum.setId(array.getJSONObject(i).getInt("id"));
					colum.setName(array.getJSONObject(i).getString("name"));
					colums.add(colum);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return colums;
	}
}
