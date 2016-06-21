package com.lzstudio.healthy.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.NewsDetailModle;
import com.lzstudio.healthy.http.HttpClientUtils;
import com.lzstudio.healthy.http.ResponseData;
import com.lzstudio.healthy.http.json.NewsDetailParser;
import com.lzstudio.healthy.view.titanic.Titanic;
import com.lzstudio.healthy.view.titanic.TitanicTextView;
import com.lzstudio.healthy.view.titanic.Typefaces;
import com.umeng.analytics.MobclickAgent;

public class NewsDetailActivity extends Activity implements ResponseData {
	private TextView tv_title, tv_back;
	private TextView tv_newsTitle, tv_time, tv_keyword, tv_content;
	private LinearLayout rl_loading;
	private TitanicTextView textView;
	private Titanic titanic;
	private NewsDetailModle newsDetail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.news_detail_activity);
		String id = getIntent().getStringExtra("newsId");
		initView();
		titanic = new Titanic();
		textView.setTypeface(Typefaces.get(this, "Satisfy-Regular.ttf"));
		titanic.start(textView);
		HttpClientUtils.getInstance().getNewsDetailJson(this, id);
	}

	private void initView() {
		tv_title = (TextView) this.findViewById(R.id.tv_title);
		tv_back = (TextView) this.findViewById(R.id.tv_back);
		tv_newsTitle = (TextView) this.findViewById(R.id.news_detail_newstitle);
		tv_time = (TextView) this.findViewById(R.id.news_detail_time);
		tv_keyword = (TextView) this.findViewById(R.id.news_detail_keyword);
		tv_content = (TextView) this.findViewById(R.id.news_detail_content);
		rl_loading = (LinearLayout) this.findViewById(R.id.news_detail_loading);
		textView = (TitanicTextView) this.findViewById(R.id.my_text_view);
		tv_title.setText(R.string.news_detail);
		tv_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	@Override
	public void getResponseData(int id, String result) {
		if (id == 0) {
			newsDetail = NewsDetailParser.getInstance()
					.jsonToNewsDetail(result);
			if (newsDetail != null) {
				tv_newsTitle.setText(newsDetail.getTitle());
				String time = newsDetail.getMediaName() + ":  "
						+ newsDetail.getTime();
				tv_time.setText(time);
				tv_keyword.setText("关键词:" + newsDetail.getKeyword());
				CharSequence content = Html.fromHtml(newsDetail.getContent()
						.replace("\n", "<br/>"));
				tv_content.setText(content);
				if (rl_loading.isShown()) {
					titanic.cancel();
					rl_loading.setVisibility(View.GONE);
				}
			} else {
				Toast.makeText(this, "获取数据失败...", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(this, "获取数据出错...", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
