package com.lzstudio.healthy.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.lzstudio.healthy.bean.Run;
import com.lzstudio.healthy.utils.ConstrantValue;
import com.lzstudio.healthy.utils.des.DES;
import com.show.api.ShowApiRequest;

public class HttpClientUtils {
	private HttpClientUtils() {
	}

	static class NewInstance {
		private static HttpClientUtils instance = new HttpClientUtils();
	}

	public static HttpClientUtils getInstance() {
		return NewInstance.instance;
	}

	/**
	 * 我参加的
	 * 
	 * @param responseData
	 * @param url
	 * @param phoneNumber
	 */
	public void joined(final ResponseData responseData, String url,
			String phoneNumber) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 立即参加
	 * 
	 * @param responseData
	 * @param url
	 * @param phoneNumber
	 * @param id
	 *            runInfo的Id
	 */
	public void joinNow(final ResponseData responseData, String url,
			String phoneNumber, String id) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("id", id);
		params.put("phoneNumber", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 发布约跑信息
	 * 
	 * @param responseData
	 * @param url
	 * @param run
	 * @param phoneNumber
	 *            手机号
	 */
	public void newRun(final ResponseData responseData, String url, Run run,
			String phoneNumber) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("title", run.getTitle());
		params.put("yuedingtime", run.getYuedingtime());
		params.put("address", run.getAddress());
		params.put("owner", run.getOwner());
		params.put("describe", run.getDescribe());
		params.put("id", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 获取正在进行的约跑信息
	 * 
	 * @param responseData
	 * @param url
	 * @param phoneNumber
	 */
	public void getRunListInfo(final ResponseData responseData, String url,
			String phoneNumber) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	public void getJsonDatas(final ResponseData responseData, String page,
			String id) {
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				try {
					String result = new String(responseBody, "utf-8");
					responseData.getResponseData(0, result);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		};
		new ShowApiRequest(Url.HEALTHY_NEWS, ConstrantValue.SHOW_API_APPID,
				ConstrantValue.SHOW_API_KEY).setResponseHandler(handler)
				.addTextPara("tid", id).addTextPara("page", page).post();
	}

	public void getNewsDetailJson(final ResponseData responseData, String newsId) {
		AsyncHttpResponseHandler handler = new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				try {
					String result = new String(responseBody, "utf-8");
					responseData.getResponseData(0, result);
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		};
		new ShowApiRequest(Url.HEALTHY_NEWS_DETAIL,
				ConstrantValue.SHOW_API_APPID, ConstrantValue.SHOW_API_KEY)
				.setResponseHandler(handler).addTextPara("id", newsId).post();
	}

	/**
	 * 注册
	 * 
	 * @return
	 */
	public void registerJson(final ResponseData responseData, String url,
			String username, String password, String phoneNumber,
			String registerTime) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		params.put("username", username);
		params.put("password", password);
		params.put("registerTime", registerTime);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 登陆
	 * 
	 * @return
	 */
	public void loginJson(final ResponseData responseData, String url,
			String phoneNumber, String password) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		params.put("password", password);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 设置
	 * 
	 * @return
	 */
	public void settingJson(final ResponseData responseData, String url,
			String phoneNumber, String username, String sex, int age,
			String path) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		params.put("username", username);
		params.put("sex", sex);
		params.put("age", age);
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		try {
			params.put("headImg", file);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 提交步数
	 * 
	 * @return
	 */
	public void paceCountJson(final ResponseData responseData, String url,
			String phoneNumber, long paceCount) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		params.put("paceCount", paceCount);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 获取排行榜
	 * 
	 * @return
	 */
	public void rankJson(final ResponseData responseData, String url, int page,
			int pageCount) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("page", page);
		params.put("pageCount", pageCount);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 话费变化
	 * 
	 * @return
	 */
	public void huafeiJson(final ResponseData responseData, String url,
			String phoneNumber, long time, String huafei) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		String desStr = "";
		try {
			desStr = DES.encryptDES(phoneNumber + time + huafei,
					ConstrantValue.DES_KEY);
		} catch (Exception e1) {
			responseData.getResponseData(-2, "加密出错");
			e1.printStackTrace();
			return;
		}
		params.put("phoneNumber", phoneNumber);
		params.put("time", time);
		params.put("huafei", huafei);
		params.put("secret", desStr);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(1, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 积分变化
	 * 
	 * @return
	 */
	public void pointJson(final ResponseData responseData, String url,
			String phoneNumber, long time, int point, int flag) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		String desStr = "";
		try {
			desStr = DES.encryptDES(phoneNumber + time + point,
					ConstrantValue.DES_KEY);
		} catch (Exception e1) {
			responseData.getResponseData(-2, "加密出错");
			e1.printStackTrace();
			return;
		}
		params.put("phoneNumber", phoneNumber);
		params.put("time", time);
		params.put("points", point);
		params.put("secret", desStr);
		params.put("flag", flag);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(2, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 兑换话费
	 * 
	 * @return
	 */
	public void duihuanJson(final ResponseData responseData, String url,
			String phoneNumber, long time, String key) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		String desStr = "";
		try {
			desStr = DES.encryptDES(phoneNumber + time + key,
					ConstrantValue.DES_KEY);
		} catch (Exception e1) {
			responseData.getResponseData(-2, "加密出错");
			e1.printStackTrace();
			return;
		}
		params.put("phoneNumber", phoneNumber);
		params.put("time", time);
		params.put("key", key);
		params.put("secret", desStr);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(3, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 查询积分
	 * 
	 * @return
	 */
	public void chaxunjifenJson(final ResponseData responseData, String url,
			String phoneNumber) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(4, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}

	/**
	 * 查询话费
	 * 
	 * @return
	 */
	public void chaxunhuafeiJson(final ResponseData responseData, String url,
			String phoneNumber) {
		AsyncHttpClient client = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("phoneNumber", phoneNumber);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] header,
					byte[] responseBody) {
				if (statusCode == 200) {
					try {
						String result = new String(responseBody, "utf-8");
						responseData.getResponseData(5, result);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onFailure(int statusCode, Header[] header,
					byte[] responseBody, Throwable e) {
				responseData.getResponseData(-1, "获取失败");
				e.printStackTrace();
			}
		});
	}
}
