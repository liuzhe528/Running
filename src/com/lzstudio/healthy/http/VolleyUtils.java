package com.lzstudio.healthy.http;

import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

public class VolleyUtils {
	public static void getVolleyData(String mUrl, Context context,
			final ResponseData responseData) {
		RequestQueue requestQueue = Volley.newRequestQueue(context);
		JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(mUrl, null,
				new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(final JSONObject response) {
						// 成功获取数据后将数据显示在屏幕上
						/*
						 * try { System.out.println(response.toString()); }
						 * catch (Exception e) { e.printStackTrace(); }
						 */
					}

				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Log.d("TAG", error.getMessage(), error);
					}
				}) {

			@Override
			protected Response<JSONObject> parseNetworkResponse(
					NetworkResponse response) {
				try {
					JSONObject jsonObject = new JSONObject(new String(
							response.data, "UTF-8"));
					responseData.getResponseData(0, jsonObject.toString());
					return Response.success(jsonObject,
							HttpHeaderParser.parseCacheHeaders(response));
				} catch (UnsupportedEncodingException e) {
					return Response.error(new ParseError(e));
				} catch (Exception je) {
					return Response.error(new ParseError(je));
				}
			}

		};
		requestQueue.add(jsonObjectRequest);
	}
}
