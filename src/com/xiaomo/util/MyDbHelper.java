package com.xiaomo.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDbHelper extends SQLiteOpenHelper {
	
	private static final String SQL_CREATE_TABLE = 
//			"CREATE TABLE t_carnumber_raid (" 
//					 + "  _id integer  primary key autoincrement ," 
//					 + "  carNumber varchar(20)  UNIQUE,"
//					 + "  illegalString varchar(200) ,"
//					 + "  imgPath varchar(200) ,"
//					 + "  isReported smallint ,"
//					 + "  createdTime varchar(50)"
//					 + ") ";
			"CREATE TABLE t_carnumber_info (" 
					 + "  _id integer  primary key," 
					 + "  car_number varchar(20) UNIQUE," 
					// + "  color varchar(5) ," 
					// + "  maker varchar(20) ," 
					 + "  type varchar(50) ," 
					// + "  vin varchar(50) ," 
					// + "  engine_no varchar(50)  ," 
					 + "  legal_number smallint ," 
					 + "  is_legal_car smallint  ," 
					 + "  is_yellow_car smallint  ," 
					 + "  is_blacklist_car smallint  ," 
					 + "  is_seized_car smallint  ," 
					 + "  is_checkok_car smallint  ," 
					 + "  is_scrapped_car smallint  ," 
					// + "  owner varchar(50) ," 
					// + "  owner_id varchar(50)  ," 
					// + "  car_color varchar(50) ," 
					 + "  img varchar(50)  ," 
					// + "  video varchar(50)  ," 
					// + "  report_police_id varchar(50) ," 
					// + "  report_police_name varchar(50) ," 
					 + "  is_reported smallint ," 
					// + "  server_carid integer ," 
					 + "  create_time varchar(50) ," 
					// + "  compare_time varchar(50)  " 
					 + "  car_type varchar(50)  " 
					 + ") ";
	
	private static final String SQL_DROP_TABLE = "drop table is exists t_carnumber_info";

	public MyDbHelper(Context context, String name,
			int version) {
		
		super(context, name, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
//		db.execSQL(SQL_DROP_TABLE);
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		db.execSQL(SQL_DROP_TABLE);
		this.onCreate(db);
	}

}
