package com.lzstudio.healthy.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzstudio.healthy.R;

public class MenuAdapter extends BaseAdapter {
	private Context context;
	private String[] titles = { "分享成绩", "给个好评", "意见反馈", "使用说明" };
	private int[] icons = { R.drawable.menu_share, R.drawable.menu_good,
			R.drawable.menu_feedback, R.drawable.menu_introduce };

	public MenuAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return titles.length;
	}

	@Override
	public Object getItem(int position) {
		return titles[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.menu_item, null);
			holder.iv_menu = (ImageView) convertView
					.findViewById(R.id.iv_menu_item);
			holder.tv_menu = (TextView) convertView
					.findViewById(R.id.tv_menu_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.iv_menu.setImageResource(icons[position]);
		holder.tv_menu.setText(titles[position]);
		return convertView;
	}

	class ViewHolder {
		ImageView iv_menu;
		TextView tv_menu;
	}
}
