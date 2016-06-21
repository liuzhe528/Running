package com.lzstudio.healthy.adapter;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.lzstudio.healthy.R;
import com.lzstudio.healthy.bean.User;
import com.lzstudio.healthy.utils.Options;
import com.lzstudio.healthy.view.ciclehead.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

//TODO  修改listView的布局完善点击图片打电话的功能  写发布约跑信息的界面，完成发布功能
public class RunnerAdapter extends BaseAdapter {
	private List<User> list;
	private Context context;
	private DisplayImageOptions options;
	private ImageLoader imageLoader = ImageLoader.getInstance();

	public RunnerAdapter(Context context, List<User> list) {
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
		final User user = list.get(position);
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View
					.inflate(context, R.layout.joined_user_item, null);
			holder.iv_head = (CircleImageView) convertView
					.findViewById(R.id.joined_user_head);
			holder.tv_username = (TextView) convertView
					.findViewById(R.id.tv_joined_username);
			holder.tv_phoneNumber = (TextView) convertView
					.findViewById(R.id.tv_joined_phoneNumber);
			holder.iv_phone = (ImageView) convertView
					.findViewById(R.id.iv_joined_phone);
			holder.iv_message = (ImageView) convertView
					.findViewById(R.id.iv_joined_message);
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
		holder.tv_phoneNumber.setText(user.getPhoneNumber());
		holder.iv_phone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ user.getPhoneNumber()));
				context.startActivity(intent);
			}
		});
		holder.iv_message.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(Intent.ACTION_SENDTO, Uri
						.parse("smsto:" + user.getPhoneNumber()));
				intent.putExtra("sms_body", "跑步");
				context.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		CircleImageView iv_head;
		TextView tv_username;
		TextView tv_phoneNumber;
		ImageView iv_phone;
		ImageView iv_message;
	}
}
