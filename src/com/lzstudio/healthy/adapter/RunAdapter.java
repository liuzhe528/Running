package com.lzstudio.healthy.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.Run;

public class RunAdapter extends BaseAdapter {
	private List<Run> list;
	private Context context;

	public RunAdapter(Context context, List<Run> list) {
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		Run run = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.news_list_item, null);
			holder.tv_intro = (TextView) convertView
					.findViewById(R.id.news_list_intro);
			holder.tv_title = (TextView) convertView
					.findViewById(R.id.news_list_title);
			holder.tv_time = (TextView) convertView
					.findViewById(R.id.news_list_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (run.isHasRead()) {
			holder.tv_title.setTextColor(context.getResources().getColor(
					R.color.news_item_has_read_textcolor));
		} else {
			holder.tv_title.setTextColor(context.getResources().getColor(
					R.color.news_item_no_read_textcolor));
		}
		holder.tv_title.setText(run.getTitle());
		holder.tv_intro.setText(run.getDescribe());
		holder.tv_time.setText("发布时间：" + run.getFabutime());
		return convertView;
	}

	class ViewHolder {
		TextView tv_title;
		TextView tv_intro;
		TextView tv_time;
	}
}
