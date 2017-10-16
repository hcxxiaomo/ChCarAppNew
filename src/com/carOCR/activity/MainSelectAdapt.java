package com.carOCR.activity;

import java.util.List;

import com.xiaomo.chcarappnew.R;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainSelectAdapt extends RecyclerView.Adapter<MainSelectAdapt.MyViewHolder> {

	
	private int total_size;
	
	   /** 
  * ItemClick的回调接口 
  * @author zhy 
  * 
  */  
 public interface OnItemClickLitener  
 {  
     void onItemClick(View view, int position);  
 }  

 private OnItemClickLitener mOnItemClickLitener;  

 public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)  
 {  
     this.mOnItemClickLitener = mOnItemClickLitener;  
 }  
	
	private LayoutInflater mInflater;
	private List<String> mDatas;

	public MainSelectAdapt(Context context, List<String> datats) {
		mInflater = LayoutInflater.from(context);
		mDatas = datats;
		total_size = mDatas.size();
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		public MyViewHolder(View arg0) {
			super(arg0);
		}

		TextView mTxt_title;
		TextView mTxt_content;
	}

	@Override
	public int getItemCount() {
//		return mDatas.size();
		return  Integer.MAX_VALUE;
	}

	/**
	 * 创建ViewHolder
	 */
	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
		View view = mInflater.inflate(R.layout.main_select_item, viewGroup, false);
		MyViewHolder viewHolder = new MyViewHolder(view);

		viewHolder.mTxt_title = (TextView) view.findViewById(R.id.id_main_select_item_title);
		viewHolder.mTxt_content = (TextView) view.findViewById(R.id.id_main_select_item_content);
		return viewHolder;
	}

	/**
	 * 设置值
	 */
//	@Override
//	public void onBindViewHolder(final MyViewHolder viewHolder, final int i) {
//		viewHolder.mTxt_title.setText((mDatas.get(i).split("_")[0]));
//		viewHolder.mTxt_content.setText((mDatas.get(i).split("_")[1]));
//	}
	
	  @Override  
	    public void onBindViewHolder(final MyViewHolder viewHolder, final int i)  
	    {  
		  viewHolder.mTxt_title.setText((mDatas.get(i % total_size).split("_")[0]));
		  viewHolder.mTxt_content.setText((mDatas.get(i % total_size).split("_")[1]));
//			viewHolder.mTxt_title.setText("11");
//			viewHolder.mTxt_content.setText("22");
//	  Log.i("-xiaomo-","i = "+ i + "i % total_size= "+ (i % total_size));
	        //如果设置了回调，则设置点击事件  
	        if (mOnItemClickLitener != null)  
	        {  
	            viewHolder.itemView.setOnClickListener(new OnClickListener()  
	            {  
	                @Override  
	                public void onClick(View v)  
	                {  
	                    mOnItemClickLitener.onItemClick(viewHolder.itemView, i);  
	                }  
	            });  
	  
	        }  
	  
	    }  

}
