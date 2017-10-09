package com.xiaomo.chcarappnew;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.carOCR.activity.ScanActivity;
import com.xiaomo.chcarappnew.activity.DefaultAreaActivity;
import com.xiaomo.chcarappnew.activity.NetworkSetActivity;
import com.xiaomo.chcarappnew.activity.StaticActivity;
import com.xiaomo.chcarappnew.adapt.MyGridViewAdapt;
import com.xiaomo.chcarappnew.view.MyGridView;

public class MainActivity extends Activity {

   private MyGridView gridview;
   
   private Intent intent = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		 gridview= (MyGridView) findViewById(R.id.gridview);
	        gridview.setAdapter(new MyGridViewAdapt(this));
	        
	        gridview.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					if (position == 3) {
						intent = new Intent(MainActivity.this, StaticActivity.class);
						startActivity(intent);
					}else if(position == 0){
						intent = new Intent(MainActivity.this, ScanActivity.class);
						startActivity(intent);
					}
					
				}
			});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		
		if (id == R.id.action_network_set) {
			intent = new Intent(this, NetworkSetActivity.class);
			startActivity(intent);
			return true;
		}else if(id == R.id.action_default_area){
			intent = new Intent(this, DefaultAreaActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}
}
