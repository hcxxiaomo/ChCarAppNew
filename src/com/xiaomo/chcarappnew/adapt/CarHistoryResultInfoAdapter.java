package com.xiaomo.chcarappnew.adapt;

import java.util.List;

import com.xiaomo.chcarappnew.R;
import com.xiaomo.chcarappnew.util.BaseViewHolder;
import com.xiaomo.db.model.CarHistoryResultInfo;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CarHistoryResultInfoAdapter   extends BaseAdapter {

	private List<CarHistoryResultInfo> mListCarHisInfo = null;
	private Context mContext;
	
	public CarHistoryResultInfoAdapter(
			List<CarHistoryResultInfo> mListCarHisInfo, Context mContext) {
		super();
		this.mListCarHisInfo = mListCarHisInfo;
		this.mContext = mContext;
	}

	@Override
	public int getCount() {
		return mListCarHisInfo.size();
	}

	@Override
	public Object getItem(int position) {
		return mListCarHisInfo.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.carnumber_raid_list_item, parent,false);
		}
		CarHistoryResultInfo ci = mListCarHisInfo.get(position);
		TextView raid_carnumber = BaseViewHolder.get(convertView, R.id.raid_carnumber);
		TextView raid_illegalstring = BaseViewHolder.get(convertView, R.id.raid_illegalstring);
		TextView raid_isreport = BaseViewHolder.get(convertView, R.id.raid_isreport);
		TextView raid_time = BaseViewHolder.get(convertView, R.id.raid_time);
		raid_carnumber.setText(ci.getCarNumber());
		
		raid_illegalstring.setText(ci.getCompareResult());
		raid_isreport.setText(ci.getUpload());
		raid_time.setText(ci.getTime());
		return convertView;
	}

}
