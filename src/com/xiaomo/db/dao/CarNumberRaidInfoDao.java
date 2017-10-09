package com.xiaomo.db.dao;

import android.database.sqlite.SQLiteDatabase;

import com.xiaomo.db.model.CarNumberRaidInfo;
import com.xiaomo.util.PageBean;

public class CarNumberRaidInfoDao {

	private SQLiteDatabase db = null;

	public CarNumberRaidInfoDao(SQLiteDatabase db) {
		super();
		this.db = db;
	}
	
	@Deprecated
	public void insert(CarNumberRaidInfo staticInfo){
		String sql = "insert into t_carnumber_raid values ( null, ?, ? , ?, ? ,?)";
		Object[] args = new Object[]{
				staticInfo.carNumber
				,staticInfo.illegalString
				,staticInfo.imgPath
				,staticInfo.isReported
				,staticInfo.createdTime
		};
		db.execSQL(sql,args);
	}
	
	public void insertReplace(CarNumberRaidInfo staticInfo){
		String sql = "REPLACE INTO t_carnumber_raid (carNumber, illegalString, imgPath, isReported, createdTime) values ( ?, ? , ?, ? ,?)";
		Object[] args = new Object[]{
				staticInfo.carNumber
				,staticInfo.illegalString
				,staticInfo.imgPath
				,staticInfo.isReported
				,staticInfo.createdTime
		};
		db.execSQL(sql,args);
	}
	
	public void findCarNumberRaidInfo (String legalType,String carNumber,String isReported,String startedDate,String endDate,PageBean pageBean){
		
	}
	
	public void deleteByCarNumber(){
		
	}
}
