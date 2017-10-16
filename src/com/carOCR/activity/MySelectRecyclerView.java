package com.carOCR.activity;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class MySelectRecyclerView  extends RecyclerView {

	 public MySelectRecyclerView(Context context, AttributeSet attrs)  
	    {  
	        super(context, attrs);  
	    }  
	  
	 private View mCurrentView;  
	 
	    /** 
	     * 滚动时回调的接口 
	     */  
	    private OnItemScrollChangeListener mItemScrollChangeListener;  
	  
	    public void setOnItemScrollChangeListener(  
	            OnItemScrollChangeListener mItemScrollChangeListener)  
	    {  
	        this.mItemScrollChangeListener = mItemScrollChangeListener;  
	    }  
	  
	    public interface OnItemScrollChangeListener  
	    {  
	        void onChange(View view, int position);  
	    }  
	  
	    @Override  
	    protected void onLayout(boolean changed, int l, int t, int r, int b)  
	    {  
	        super.onLayout(changed, l, t, r, b);  
	  
	        mCurrentView = getChildAt(0);  
	  
	        if (mItemScrollChangeListener != null)  
	        {  
	            mItemScrollChangeListener.onChange(mCurrentView,  
	                    getChildPosition(mCurrentView));  
	        }  
	    }  
	  
	    @Override  
	    public boolean onTouchEvent(MotionEvent e)  
	    {  
	  
	        if (e.getAction() == MotionEvent.ACTION_MOVE)  
	        {  
	            mCurrentView = getChildAt(0);  
	            // Log.e("TAG", getChildPosition(getChildAt(0)) + "");  
	            if (mItemScrollChangeListener != null)  
	            {  
	                mItemScrollChangeListener.onChange(mCurrentView,  
	                        getChildPosition(mCurrentView));  
	  
	            }  
	  
	        }  
	  
	        return super.onTouchEvent(e);  
	    }  
	 
	 @Override
	 public void setAdapter(Adapter adapter) {
	     super.setAdapter(adapter);
	     scrollToPosition(getAdapter().getItemCount() * 10000);//开始时的偏移量
	 }
}
