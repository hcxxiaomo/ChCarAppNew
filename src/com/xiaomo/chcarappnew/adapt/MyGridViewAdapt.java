package com.xiaomo.chcarappnew.adapt;

import com.xiaomo.chcarappnew.R;
import com.xiaomo.chcarappnew.util.BaseViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyGridViewAdapt extends BaseAdapter {

	private Context mContext;
	
	public MyGridViewAdapt(Context mContext) {
		super();
		this.mContext = mContext;
	}

	private String[] img_text = {"车辆缉查","违法抓拍取证","白名单","统计","车载"};
	private int[] imgs = {R.drawable.icon_new_2,R.drawable.icon_new_1,R.drawable.icon_new_3,R.drawable.icon_new_4,R.drawable.icon_new_5};
	
	@Override
	public int getCount() {
		return img_text.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent,false);
		}
		TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
		ImageView  iv = BaseViewHolder.get(convertView, R.id.iv_item);
		iv.setBackgroundResource(imgs[position]);
		tv.setText(img_text[position]);
		if (position == 1 || position == 2) {
			convertView.setBackgroundResource(R.drawable.bg_gv_y);
//			(R.color.icon_yellow);
		}else{
			convertView.setBackgroundResource(R.drawable.bg_gv);
		}
		return convertView;
	}

	
	
}
