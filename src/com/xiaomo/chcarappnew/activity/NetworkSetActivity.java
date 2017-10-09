package com.xiaomo.chcarappnew.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.xiaomo.chcarappnew.R;

public class NetworkSetActivity  extends Activity {
	
	
	private EditText et_network_set_server;
	private EditText et_network_set_port;
	private EditText et_network_set_dir;
	private Button bt_network_set;
	private SharedPreferences sp ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_network_set);
		
		bt_network_set = (Button) findViewById(R.id.bt_network_set);
		et_network_set_server = (EditText) findViewById(R.id.et_network_set_server);
		et_network_set_port = (EditText) findViewById(R.id.et_network_set_port);
		et_network_set_dir = (EditText) findViewById(R.id.et_network_set_dir);
		
		sp =  getSharedPreferences("network_set", MODE_PRIVATE);
		et_network_set_server.setText(sp.getString("server", "117.27.138.166"));
		et_network_set_port.setText(sp.getString("port", "8080"));
		et_network_set_dir.setText(sp.getString("dir", "carplatenumber"));
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		//actionBar.setHomeAsUpIndicator(R.drawable.ic_drawer); //修改actionbar左上角返回按钮的图标
		
		bt_network_set.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (et_network_set_server.getText().length() > 0
						&& et_network_set_port.getText().length() > 0
						) {
					SharedPreferences.Editor editor = sp.edit();
					editor.putString("server", et_network_set_server.getText().toString());
					editor.putString("port", et_network_set_port.getText().toString());
					editor.putString("dir", et_network_set_dir.getText().toString());
					editor.commit();
				}else{
					Toast.makeText(NetworkSetActivity.this, "服务器地址及端口号均不能为空！", Toast.LENGTH_SHORT).show();
				}
				
				
				
			}
		});

	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
//		int id = item.getItemId();
		if (item.getItemId() == android.R.id.home) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
