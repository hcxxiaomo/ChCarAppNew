package com.carOCR.activity;

import com.szOCR.general.CGlobal;
import com.xiaomo.chcarappnew.R;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PopupPassword implements View.OnClickListener
{
	public View parent;
	public PopupWindow popupWindow;
		
	public Button m_btnOk;
	public Button m_btnCacel;
	public TextView m_txtDeviceKey;
	public EditText m_editVerifyKey;
	
	public PopupPassword(Context paramContext)
	{
		this.parent = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(R.layout.password_key, null,true);		
		m_btnOk = (Button) this.parent.findViewById(R.id.btnOk);
		m_btnCacel = (Button) this.parent.findViewById(R.id.btnCancel);
		m_txtDeviceKey = (TextView)this.parent.findViewById(R.id.txtDeviceKey);
		m_editVerifyKey = (EditText)this.parent.findViewById(R.id.editVerifyKey);
		this.popupWindow = new PopupWindow(this.parent,LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT,true);		
		this.popupWindow.setOutsideTouchable(false);
		//m_btnCacel.setOnClickListener(this);	
		
	}
	public void showAtLocation(int left,int top)
	{
	
		this.popupWindow.showAtLocation(this.parent, Gravity.CENTER, left, top);
		
		m_txtDeviceKey.setText(CGlobal.g_devicekey);
	}
	
	public void hide()
	{
		this.popupWindow.dismiss();
	}
	public boolean isVisible()
	{
		return this.popupWindow.isShowing();
	}
	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch(v.getId())
		{		
			case R.id.btnCancel:
				hide();
				break;
		}
	}
}