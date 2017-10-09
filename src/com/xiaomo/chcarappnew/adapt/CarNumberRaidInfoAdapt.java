package com.xiaomo.chcarappnew.adapt;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaomo.chcarappnew.R;
import com.xiaomo.chcarappnew.util.BaseViewHolder;
import com.xiaomo.db.model.CarNumberRaidInfo;

public class CarNumberRaidInfoAdapt extends BaseAdapter {

	private Context mContext;
	private List<CarNumberRaidInfo> listCarNumberRaidInfo;
	
	public CarNumberRaidInfoAdapt(Context mContext , List<CarNumberRaidInfo> listCarNumberRaidInfo) {
		super();
		this.mContext = mContext;
		this.listCarNumberRaidInfo = listCarNumberRaidInfo;
	}

	@Override
	public int getCount() {
		return listCarNumberRaidInfo.size();
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
			convertView = LayoutInflater.from(mContext).inflate(R.layout.carnumber_raid_list_item, parent,false);
		}
		TextView raid_carnumber = BaseViewHolder.get(convertView, R.id.raid_carnumber);
		TextView raid_illegalstring = BaseViewHolder.get(convertView, R.id.raid_illegalstring);
		TextView raid_isreport = BaseViewHolder.get(convertView, R.id.raid_isreport);
		TextView raid_time = BaseViewHolder.get(convertView, R.id.raid_time);
		raid_carnumber.setText(listCarNumberRaidInfo.get(position).carNumber);
		raid_illegalstring.setText(listCarNumberRaidInfo.get(position).illegalString);
		raid_isreport.setText(listCarNumberRaidInfo.get(position).isReported);
		raid_time.setText(listCarNumberRaidInfo.get(position).createdTime);
		return convertView;
	}

	
	
}
