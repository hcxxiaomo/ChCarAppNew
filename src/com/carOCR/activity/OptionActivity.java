package com.carOCR.activity;

import java.util.List;

import com.szOCR.general.CGlobal;
import com.szOCR.general.Defines;
import com.xiaomo.chcarappnew.R;
import com.xiaomo.util.MyDbHelper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class OptionActivity extends FragmentActivity
{
	public static final int OPTIONACTIVITY_REQUESTCODE = 100;
	
	public static final int OPTION_SELECTZOOM = 0;
	public static final int OPTION_SELECTPROV = 1;
	public static final int OPTION_SELECTRECORD = 2;
	public static final int OPTION_INPUTIDS = 3;
	
	public int m_iSelOptionPage = 0;
	
	/*//数据库处理相关部分
	private static final String DB_NAME = "t_car_license.db";
	private MyDbHelper helper  = null;
	private CarLicenseDao carLicenseDao = null;*/
	
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_option);
        
		Intent intent=getIntent();  
		m_iSelOptionPage = (int)intent.getIntExtra("OptionPage", OPTION_SELECTZOOM);		
		selectOptionPage(m_iSelOptionPage);
		
//		helper = new  MyDbHelper(this, DB_NAME, 1);
	}
	
	public void selectOptionPage(int iPage)
	{
		Fragment newFragment = null;
				
		
		if(iPage == OPTION_SELECTZOOM)
		{
			newFragment = new Frag_SelZoom();
		}
		else if(iPage == OPTION_SELECTPROV)
		{
			newFragment = new Frag_SelProv();
		}
		else if(iPage == OPTION_SELECTRECORD)
		{
			newFragment = new Frag_SelRecord();
		}
//		else if(iPage == OPTION_INPUTIDS)
//		{
//			newFragment = new Frag_Input_Ids();
//		}
		else
		{
			return;
		}
				
		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

		transaction.replace(R.id.idcMainView, newFragment);

		// Commit the transaction
		transaction.commit();
		m_iSelOptionPage = iPage;
	}
	
	public static void saveOptionData(Context con)
	{
		SharedPreferences sp = con.getSharedPreferences("option_data", Context.MODE_PRIVATE);
		sp.edit().putInt("zoom_index", CGlobal.g_nCameraZoomFactor).apply();
	    sp.edit().putInt("prov_index", CGlobal.g_provinceId).apply();
	    sp.edit().putInt("runmode", CGlobal.g_runMode).apply();
	}
	
	public static void loadOptionData(Context con)
	{
		SharedPreferences p = con.getSharedPreferences("option_data", MODE_PRIVATE);
        int zoomIndex = p.getInt("zoom_index", 0);
        int provIndex = p.getInt("prov_index", 0);
        int runMode = p.getInt("rummode", Defines.RUNMODE_NONRECORD);
        
        CGlobal.g_nCameraZoomFactor = zoomIndex;
        CGlobal.g_provinceId = provIndex;
        CGlobal.g_runMode = runMode;
	}
	
	public static class Frag_SelZoom extends ListFragment 
	{
		static final String[] ZOOM_LIST_STRING = {"0~4m", "4~8m", "8~12m", "12~16m", "16~20m"};
		static final double[] ZOOM_LIST = {0, 0.25, 0.5, 0.75, 1};
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) 
		{


			ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.listitem_option, ZOOM_LIST_STRING) ;
	        setListAdapter(adapter);
	        
	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
		
		@Override
		public void onResume()
		{
			super.onResume();
			int listIndex = 0;
			if(CGlobal.g_nCameraZoomFactor >= 0)
	        {
				ListView listView = getListView();
    	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	        
    	        double minDiffValue = Double.MAX_VALUE;
    	        
    	        for(int i=0;i<ZOOM_LIST.length; i++)
    	        {
    	        	double diff = Math.abs(ScanActivity.mZoomMax * ZOOM_LIST[i] - CGlobal.g_nCameraZoomFactor);
    	        	if(minDiffValue > diff)
    	        	{
    	        		minDiffValue = diff;
    	        		listIndex = i;
    	        	}
    	        }
    	        
    	        listView.setItemChecked(listIndex,true);
        	}
		}
		
	    @Override
	    public void onListItemClick (ListView l, View v, int position, long id)
	    {
	        // get TextView's Text.
	        String strText = (String) l.getItemAtPosition(position);	        
	        CGlobal.g_nCameraZoomFactor = (int)(ScanActivity.mZoomMax * ZOOM_LIST[position]);
	        
	        OptionActivity.saveOptionData(getActivity());
	        
	        ListView listView = getListView();
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        listView.setItemChecked(position,true);
	        
	        getActivity().finish();
	        // TODO
	    }
	}

	public static class Frag_SelProv extends ListFragment 
	{
		static final String[] PROV_STRING_LIST = {"全部", "京", "津", "晋", "冀",  "蒙", "辽", "吉", "黑", "沪", "苏", "浙", 
			"皖", "闽", "赣", "鲁", "豫", "鄂", "湘", "粤", "桂", "琼", "川", "贵", "云", "藏", "陕", "甘", "青", "宁", "新","渝"};
		static final int PROV_BASEINDEX = 36;
		
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) 
		{

			ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.listitem_option, PROV_STRING_LIST) ;
	        setListAdapter(adapter);   
	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
		
		@Override
		public void onResume()
		{
			super.onResume();
			int listIndex = 0;
			if(CGlobal.g_provinceId != 0)
	        {
				listIndex = CGlobal.g_provinceId - PROV_BASEINDEX;	        		        	
	        }			
			
			if(listIndex >= 0 && listIndex < PROV_STRING_LIST.length + 1)
        	{	        		
        		ListView listView = getListView();
    	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    	        listView.setItemChecked(listIndex,true);
        	}
		}
		
	    @Override
	    public void onListItemClick (ListView l, View v, int position, long id)
	    {
	        // get TextView's Text.
	    	
	        String text = (String) l.getItemAtPosition(position) ;
	        
	        if(position == 0)
	        {
	        	CGlobal.g_provinceId = position;
	        }
	        else
	        {
	        	CGlobal.g_provinceId = position + PROV_BASEINDEX;
	        }
	        
			
	        CGlobal.outputToast(getActivity(), text);
	        
	        OptionActivity.saveOptionData(getActivity());
	        
	        ListView listView = getListView();
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        listView.setItemChecked(position,true);
	        
	        getActivity().finish();	        
	    }
	}
	
	public static class Frag_SelRecord extends ListFragment 
	{
		static final String[] RECORD_STRING_LIST = {"识别，但不录像", "边识别，边录像"};		
				
		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) 
		{

			ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.listitem_option, RECORD_STRING_LIST) ;
	        setListAdapter(adapter);   
	        return super.onCreateView(inflater, container, savedInstanceState);
	    }
		
		@Override
		public void onResume()
		{
			super.onResume();
			int listIndex = 0;
			
			listIndex = CGlobal.g_runMode;
			
			ListView listView = getListView();
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        listView.setItemChecked(listIndex,true);
		}
		
	    @Override
	    public void onListItemClick (ListView l, View v, int position, long id)
	    {
	        // get TextView's Text.
	    	
	        String text = (String) l.getItemAtPosition(position) ;
	        
	        CGlobal.g_runMode = position;
	        
	        CGlobal.outputToast(getActivity(), text);
	        
	        OptionActivity.saveOptionData(getActivity());
	        
	        ListView listView = getListView();
	        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
	        listView.setItemChecked(position,true);
	        
	        getActivity().finish();	        
	    }
	}
	/*	public  class Frag_Input_Ids extends Fragment 
	{
//		private CarLicenseDao carLicenseDao = null;
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) 
		{
			
			View rootView = inflater.inflate(R.layout.frag_input_ids, container,false);
			return rootView;
		}
		
	}
	
	public void flagInputIdsSave(View v){
		carLicenseDao = new CarLicenseDao(helper.getReadableDatabase());
		EditText owner_name = (EditText) super.findViewById(R.id.frag_input_ids_owner_name);
		EditText car_number = (EditText) super.findViewById(R.id.frag_input_ids_car_number);
		EditText illegal_type = (EditText) super.findViewById(R.id.frag_input_ids_illegal_type);
		Log.i("---xiaomo---", owner_name.getText().toString());
		CarLicense carLicense = new CarLicense(owner_name.getText().toString(),car_number.getText().toString(),illegal_type.getText().toString());
		carLicenseDao.addCarLicense(carLicense);
		Log.i("---xiaomo---","添加数据成功");
//		Toast.makeText(OptionActivity.this, "添加数据成功", Toast.LENGTH_SHORT).show();
		 CGlobal.outputToast(OptionActivity.this, "添加数据成功");
		this.finish();
	}*/
}
