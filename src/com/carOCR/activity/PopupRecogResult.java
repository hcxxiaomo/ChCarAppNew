package com.carOCR.activity;


import com.szOCR.general.CGlobal;
import com.xiaomo.chcarappnew.R;
import com.xiaomo.util.MyDbHelper;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PopupRecogResult implements View.OnClickListener
{
	
	public View parent;
	public PopupWindow mPopupWindow;
		
	public Button m_btnOk;
	public Button m_btn_car_number_start_compare;
	public Button m_btnCacel;
	public EditText m_txtRecogData;
	public EditText m_txtType;
	public ImageView m_RecogImageView;
	public Context con;
	
	//��ʾ����
	private TextView popup_silenct_canNumber;
	private TextView txt_szocr_result;
	private LinearLayout resut_list_for_show;
	private  EditText m_txtOnwerName;
	private  EditText m_txtSex;
	private  EditText m_txtAge;
	private  EditText m_txtNativePlace;
	private  EditText m_txtIdNumber;
	private  EditText m_txtTelephone;
	private  EditText m_txtMobilePhone;
	private  EditText m_txtEmail;
	private  EditText m_txtQQ;
	private  EditText m_txtWechat;
	private  EditText m_txtAddress;
	private  EditText m_txtFramNumber;
	private  EditText m_txtIllegalType;
	
	//��ݿ⴦����ز���
//	private static final String DB_NAME = "t_car_license.db";
//	private MyDbHelper helper  = null;
//	private CarLicenseDao carLicenseDao = null;
	
	public PopupRecogResult(Context paramContext)
	{
		con = paramContext;
		parent = ((LayoutInflater)paramContext.getSystemService("layout_inflater")).inflate(R.layout.popup_silenct_recogresult, null,true);		
//		m_btnOk = (Button) this.parent.findViewById(R.id.btnSignUp);
//		m_btn_car_number_start_compare = (Button) this.parent.findViewById(R.id.car_number_start_compare);
//		m_btnCacel = (Button) this.parent.findViewById(R.id.btnCancel);
//		m_txtRecogData = (EditText)this.parent.findViewById(R.id.txtItemRecogData);
//		m_txtType = (EditText)this.parent.findViewById(R.id.txtItemType);
//		m_RecogImageView = (ImageView)this.parent.findViewById(R.id.imageCar);
		mPopupWindow = new PopupWindow(this.parent,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT,true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());  
		mPopupWindow.setOutsideTouchable(false);
		
		popup_silenct_canNumber =  (TextView) this.parent.findViewById(R.id.popup_silenct_canNumber);

	}
	public void showAtLocation(View pView,int left,int top)
	{
		this.mPopupWindow.showAtLocation(pView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL , left, top);
		popup_silenct_canNumber.setText(CGlobal.g_RecogResult.m_szRecogTxt[0]);
	}
	public void hide()
	{
		this.mPopupWindow.dismiss();
	}
	public boolean isVisible()
	{
		return this.mPopupWindow.isShowing();
	}
	@Override
	public void onClick(View v)
	{
		Log.i("-xiaomo-", "PopupRecogResult.onClick"+v.getId());
		switch(v.getId())
		{		
			case R.id.btnOk:
				hide();
				Log.i("-xiaomo-", "R.id.btnOk");
				break;
			case R.id.btnCancel:
				hide();
				Log.i("-xiaomo-", "R.id.btnCancel");
				break;
//			case R.id.car_number_start_compare:
//				PopupWindowCarCheckResultInfoScan pCarResultInfo = new PopupWindowCarCheckResultInfoScan((Activity)con);
//				hide();
//				pCarResultInfo.showAtLocation(parent, Gravity.TOP | Gravity.START, 0, 0);
//				CGlobal.carPath =  CGlobal.SaveRecogBitmap("", CGlobal.myEngine.getRecgBitmap());
//				((ScanActivity)con).m_scanHandler.sendEmptyMessage(R.id.restart_preview);
				
//				Log.i("-xiaomo-", ((ScanActivity)con).toString());
//				Log.i("-xiaomo-", "car_number_start_compare");
//				break;
		}
	}
	
	

}