package com.lzstudio.healthy.adapter;

import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.User;
import com.lzstudio.healthy.utils.Options;
import com.lzstudio.healthy.view.ciclehead.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public class RankAdapter extends BaseAdapter {
	private List<User> list;
	private Context context;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public RankAdapter(Context context, List<User> list) {
		this.context = context;
		this.list = list;
		options = Options.getListOptions();
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
		User user = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.rank_item, null);
			holder.iv_head = (CircleImageView) convertView
					.findViewById(R.id.rank_user_head);
			holder.tv_username = (TextView) convertView
					.findViewById(R.id.tv_rank_username);
			holder.tv_rank = (TextView) convertView
					.findViewById(R.id.tv_rank_rank);
			holder.tv_paceCount = (TextView) convertView
					.findViewById(R.id.tv_rank_paceCount);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (!TextUtils.isEmpty(user.getHeadImg())) {
			imageLoader
					.displayImage(user.getHeadImg(), holder.iv_head, options);
		} else {
			holder.iv_head.setImageResource(R.drawable.ic_login_head);
		}
		holder.tv_username.setText(user.getUsername());
		holder.tv_paceCount.setText(user.getPaceCount() + "步");
		if (position == 0) {
			holder.tv_rank.setTextColor(0xffff0000);
		} else if (position == 1) {
			holder.tv_rank.setTextColor(0xffF7FB04);
		} else if (position == 2) {
			holder.tv_rank.setTextColor(0xffBFA345);
		} else {
			holder.tv_rank.setTextColor(0xff000000);
		}
		int pos = position + 1;
		holder.tv_rank.setText("第" + pos + "名");
		return convertView;
	}

	class ViewHolder {
		CircleImageView iv_head;
		TextView tv_username;
		TextView tv_paceCount;
		TextView tv_rank;
	}
}
