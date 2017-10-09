package com.xiaomo.chcarappnew.activity;

import java.util.Calendar;

import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.xiaomo.chcarappnew.R;
import com.xiaomo.chcarappnew.adapt.SpinnerAdapter;

public class StaticConditionActivity  extends Activity {
	
	private Spinner sn_static_condition_action;
	private Spinner sn_static_condition_upload;
	private EditText et_tatic_condition_carnumber;
	private Button bt_static_condition_starttime;
	private Button bt_static_condition_endtime;
	
	   private int year;
	    private int month;
	    private int day;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_static_condition);
		sn_static_condition_action = (Spinner) findViewById(R.id.sn_static_condition_action);
		sn_static_condition_upload = (Spinner) findViewById(R.id.sn_static_condition_upload);
		et_tatic_condition_carnumber = (EditText) findViewById(R.id.et_tatic_condition_carnumber);
		bt_static_condition_starttime = (Button) findViewById(R.id.bt_static_condition_starttime);
		bt_static_condition_endtime = (Button) findViewById(R.id.bt_static_condition_endtime);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer); //修改actionbar左上角返回按钮的图标
		
		final String[] illeagl_item = {"全部","逾期未年审","报废车","黄标车","布控车","违法未处理" };
		final String[] illeagl_upload_item = {"全部","未上报","已上报" };
		 //初始化Calendar日历对象
        Calendar mycalendar=Calendar.getInstance();
		year=mycalendar.get(Calendar.YEAR); //获取Calendar对象中的年
        month=mycalendar.get(Calendar.MONTH);//获取Calendar对象中的月
        day=mycalendar.get(Calendar.DAY_OF_MONTH);//获取这个月的第几天
        
        
        SpinnerAdapter adapter = new SpinnerAdapter(this,
	            android.R.layout.simple_spinner_dropdown_item, illeagl_item);
        sn_static_condition_action.setAdapter(adapter);
        SpinnerAdapter adapter_upload = new SpinnerAdapter(this,
        		android.R.layout.simple_spinner_dropdown_item, illeagl_upload_item);
        sn_static_condition_upload.setAdapter(adapter_upload);
        
        bt_static_condition_starttime.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //创建DatePickerDialog对象
                DatePickerDialog dpd=new DatePickerDialog(StaticConditionActivity.this,startedDatelistener,year,month,day);
                dpd.show();//显示DatePickerDialog组件
            }
        });    
        bt_static_condition_endtime.setOnClickListener(new OnClickListener(){
        	@Override
        	public void onClick(View v)
        	{
        		//创建DatePickerDialog对象
        		DatePickerDialog dpd=new DatePickerDialog(StaticConditionActivity.this,endDatelistener,year,month,day);
        		dpd.show();//显示DatePickerDialog组件
        	}
        });

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	
	 private DatePickerDialog.OnDateSetListener startedDatelistener=new DatePickerDialog.OnDateSetListener()
     {
         /**params：view：该事件关联的组件
          * params：myyear：当前选择的年
          * params：monthOfYear：当前选择的月
          * params：dayOfMonth：当前选择的日
          */
         @Override
         public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
             //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
             year=myyear;
             month=monthOfYear;
             day=dayOfMonth;
             //更新日期
             updateDate();
         }
         //当DatePickerDialog关闭时，更新日期显示
         private void updateDate()
         {
        	 StringBuilder sb = new StringBuilder();
        	 sb.append(year).append("-");
        	 if ((month+1) < 10) {
        		 sb.append("0").append((month+1)).append("-");
			}else{
				 sb.append((month+1)).append("-");
			}
        	 if(day < 10){
        		 sb.append("0").append(day);
        	 }else{
        		 sb.append(day);
        	 }
        	 Log.i("-xiaomo-", sb.toString());
        	 bt_static_condition_starttime.setText(sb.toString());
        	 bt_static_condition_starttime.setTextColor(Color.BLUE);
         }
     };
     private DatePickerDialog.OnDateSetListener endDatelistener=new DatePickerDialog.OnDateSetListener()
     {
    	 /**params：view：该事件关联的组件
    	  * params：myyear：当前选择的年
    	  * params：monthOfYear：当前选择的月
    	  * params：dayOfMonth：当前选择的日
    	  */
    	 @Override
    	 public void onDateSet(DatePicker view, int myyear, int monthOfYear,int dayOfMonth) {
    		 //修改year、month、day的变量值，以便以后单击按钮时，DatePickerDialog上显示上一次修改后的值
    		 year=myyear;
    		 month=monthOfYear;
    		 day=dayOfMonth;
    		 //更新日期
    		 updateDate();
    	 }
    	 //当DatePickerDialog关闭时，更新日期显示
    	 private void updateDate()
    	 {
    		 StringBuilder sb = new StringBuilder();
        	 sb.append(year).append("-");
        	 if ((month+1) < 10) {
        		 sb.append("0").append((month+1)).append("-");
			}else{
				 sb.append((month+1)).append("-");
			}
        	 if(day < 10){
        		 sb.append("0").append(day);
        	 }else{
        		 sb.append(day);
        	 }
        	 Log.i("-xiaomo-", sb.toString());
        	 bt_static_condition_endtime.setText(sb.toString());
        	 bt_static_condition_endtime.setTextColor(Color.BLUE);
    	 }
     };
}
