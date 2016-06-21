package com.lzstudio.healthy.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.adapter.CityAdapter;
import com.lzstudio.healthy.bean.CityItem;
import com.lzstudio.healthy.dao.CityData;
import com.lzstudio.healthy.view.city.ContactItemInterface;
import com.lzstudio.healthy.view.city.ContactListViewImpl;
import com.umeng.analytics.MobclickAgent;

public class ChooseCityActivity extends Activity implements OnItemClickListener {
	private Context context_;
	protected ContactListViewImpl listview;
	protected TextView mTitle;
	protected EditText searchBox;
	private String searchString;
	private CityAdapter adapter;
	private TextView mBack;
	private TextView mLocation;
	private Object searchLock;
	boolean inSearchMode = false;

	private final static String TAG = "ChooseCityActivity";

	List<ContactItemInterface> contactList;
	List<ContactItemInterface> filterList;
	private SearchListTask curSearchTask = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choose_city);
		init();
		initView();
	}

	public void init() {
		listview = (ContactListViewImpl) this.findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		mTitle = (TextView) this.findViewById(R.id.tv_title);
		searchBox = (EditText) this.findViewById(R.id.input_search_query);
		mBack = (TextView) this.findViewById(R.id.tv_back);
		mBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mLocation = (TextView) this.findViewById(R.id.tv_menu);
		mLocation.setVisibility(View.GONE);
		context_ = ChooseCityActivity.this;
		searchLock = new Object();
		filterList = new ArrayList<ContactItemInterface>();
		contactList = CityData.getSampleContactList();
		adapter = new CityAdapter(this, R.layout.city_item, contactList);
	}

	public void initView() {
		listview.setFastScrollEnabled(true);
		listview.setAdapter(adapter);
		mTitle.setText("选择城市");
		searchBox.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
		});
	}

	public void textChanged(Editable s) {
		searchString = searchBox.getText().toString().trim().toUpperCase();
		if (curSearchTask != null
				&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
			try {
				curSearchTask.cancel(true);
			} catch (Exception e) {
				Log.i(TAG, "Fail to cancel running search task");
			}

		}
		curSearchTask = new SearchListTask();
		curSearchTask.execute(searchString);
	}

	private class SearchListTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			filterList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode) {
				// get all the items matching this
				for (ContactItemInterface item : contactList) {
					CityItem contact = (CityItem) item;

					boolean isPinyin = contact.getFullName().toUpperCase()
							.indexOf(keyword) > -1;
					boolean isChinese = contact.getNickName().indexOf(keyword) > -1;

					if (isPinyin || isChinese) {
						filterList.add(item);
					}

				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {

			synchronized (searchLock) {

				if (inSearchMode) {
					CityAdapter adapter = new CityAdapter(context_,
							R.layout.city_item, filterList);
					adapter.setInSearchMode(true);
					listview.setInSearchMode(true);
					listview.setAdapter(adapter);
				} else {
					CityAdapter adapter = new CityAdapter(context_,
							R.layout.city_item, contactList);
					adapter.setInSearchMode(false);
					listview.setInSearchMode(false);
					listview.setAdapter(adapter);
				}
			}

		}
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		List<ContactItemInterface> searchList = inSearchMode ? filterList
				: contactList;
		Intent intent = new Intent();
		intent.putExtra("cityname", searchList.get(position).getDisplayInfo());
		this.setResult(1001, intent);
		this.finish();
	}

}
