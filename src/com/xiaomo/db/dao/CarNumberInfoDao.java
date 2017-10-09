package com.xiaomo.db.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.xiaomo.db.model.CarNumberInfo;
import com.xiaomo.db.model.PiePojo;
import com.xiaomo.util.PageBean;

public class CarNumberInfoDao {

	private SQLiteDatabase db = null;

	public CarNumberInfoDao(SQLiteDatabase db) {
		super();
		this.db = db;
	}
	
	public void insertCarNumber( CarNumberInfo cni,String policeId,String policeName){
		String sql = "REPLACE into t_carnumber_info (car_number ,legal_number, is_legal_car,is_yellow_car,is_blacklist_car,is_seized_car,is_checkok_car,is_scrapped_car,img, is_reported,create_time) values "
				+ " (  ?, ?, ?, ?, ?, ?, ? , ?, ? ,? ,?)";
		Object[] args = new Object[]{
				cni.getCarNumber()
				//,cni.getColor()
				//,cni.getMaker()
				//,cni.getType()
				//,cni.getVin()
				//,cni.getEngineNo()
				,cni.getLegalNumber()
				,cni.getIsLegalCar()
				,cni.getIsYellowCar()
				,cni.getIsBlackListCar()
				,cni.getIsSeizedCar()
				,cni.getIsCheckOkCar()
				,cni.getIsScrappedCar()
				//,cni.getOwner()
				//,cni.getOwnerId()
				//,cni.getCarColor()
				,cni.getImg()
				//,cni.getVideo()
				//,policeId
				//,policeName
				,cni.getIsReported()
				//,cni.getServerCarId()
				,cni.getCreateTime()
				//,cni.getCompareTime()
				//,cni.getCarType()
		};
		db.execSQL(sql,args);
		Log.i("---xiaomo---", "insert-->" + cni.toString());
	}
	
	public List<CarNumberInfo> findCarNumberInfo(String legalType,String carNumber,String isReported,String startedDate,String endDate,PageBean pageBean){
		List<CarNumberInfo> list = new ArrayList<CarNumberInfo>();
		StringBuilder sb = new StringBuilder();
		sb.append("select _id,car_number,is_legal_car,is_yellow_car,is_blacklist_car,is_seized_car,is_checkok_car,is_scrapped_car,legal_number,")
		.append("img,is_reported,create_time from t_carnumber_info  ");
//		String[] illeagl_item = {"全部","逾期未年审","报废车","黄标车","布控车","违法未处理" };
//		String[] illeagl_upload_item = {"全部","未上报","已上报" };
		StringBuilder sbInner = new StringBuilder();
		if ("逾期未年审".equals(legalType)) {
			sbInner.append("and is_checkok_car = 1 ");
		}else if("报废车".equals(legalType)){
			sbInner.append("and is_scrapped_car = 1 ");
		}else if("黄标车".equals(legalType)){
			sbInner.append("and is_yellow_car = 1 ");
		}else if("布控车".equals(legalType)){
			sbInner.append("and is_blacklist_car = 1 ");
		}else if("违法未处理".equals(legalType)){
			sbInner.append("and is_legal_car = 1 ");
		}
		
		if (carNumber != null && carNumber.trim().length() > 1) {
			sbInner.append("and car_number = " + carNumber);
		}
		
		if ("未上报".equals(isReported)) {
			sbInner.append("and is_reported = 0 ");
		}else if("已上报".equals(isReported)){
			sbInner.append("and is_reported = 1 ");
		}
		
		if (startedDate != null && !"开始时间".equals(startedDate)) {
			sbInner.append("and create_time >= '").append(startedDate).append("00:00:00' ");
		}
		if (endDate != null && !"结束时间".equals(endDate)) {
			sbInner.append("and create_time <= '").append(endDate).append("23:59:59' ");
		}
		
		if (sbInner.length() > 0) {
			sbInner.replace(0, 3, "where");
		}
		sb.append(sbInner).append("  order by _id desc limit ? , ?");
//		String sql = "select _id,car_number,is_legal_car,is_yellow_car,is_blacklist_car,is_seized_car,is_checkok_car,is_scrapped_car,legal_number,"
//				+ "img,video,is_reported,create_time from t_carnumber_info "
//				+ " order by _id desc limit ? , ?";
		String[] args = new String[]{
				String.valueOf(pageBean.getStart()),
				String.valueOf(pageBean.getPageSize())
		};
		Log.i("-xiaomo-",sb.toString() + " ||pageBean.getStart()="+pageBean.getStart()+"\tpageBean.getPageSize()="+pageBean.getPageSize());
		Cursor result =  db.rawQuery(sb.toString(), args);
		CarNumberInfo car = null;
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			car =new CarNumberInfo();
			car.setCarId(result.getLong(0));
			car.setCarNumber(result.getString(1));
			car.setIsLegalCar(result.getInt(2));
			car.setIsYellowCar(result.getInt(3));
			car.setIsBlackListCar(result.getInt(4));
			car.setIsSeizedCar(result.getInt(5));
			car.setIsCheckOkCar(result.getInt(6));
			car.setIsScrappedCar(result.getInt(7));
			car.setLegalNumber(result.getInt(8));
			car.setImg(result.getString(9));
			//car.setVideo(result.getString(10));
			car.setIsReported(result.getInt(10));
			car.setCreateTime(result.getString(11));
			list.add(car);
		}
//		for (int i = 0; i < list.size(); i++) {
			Log.i("-xiaomo-", "-----out----"+list.size());
//		}

		return list;
	}
	
	public int getCount(String legalType,String carNumber,String isReported,String startedDate,String endDate){
		int count = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("select count(1) from t_carnumber_info  ");
//		String[] illeagl_item = {"全部","逾期未年审","报废车","黄标车","布控车","违法未处理" };
//		String[] illeagl_upload_item = {"全部","未上报","已上报" };
		StringBuilder sbInner = new StringBuilder();
		if ("逾期未年审".equals(legalType)) {
			sbInner.append("and is_checkok_car = 1 ");
		}else if("报废车".equals(legalType)){
			sbInner.append("and is_scrapped_car = 1 ");
		}else if("黄标车".equals(legalType)){
			sbInner.append("and is_yellow_car = 1 ");
		}else if("布控车".equals(legalType)){
			sbInner.append("and is_blacklist_car = 1 ");
		}else if("违法未处理".equals(legalType)){
			sbInner.append("and is_legal_car = 1 ");
		}
		
		if ("未上报".equals(isReported)) {
			sbInner.append("and is_reported = 0 ");
		}else if("已上报".equals(isReported)){
			sbInner.append("and is_reported = 1 ");
		}
		if (carNumber != null && carNumber.trim().length() > 1) {
			sbInner.append("and car_number = " + carNumber);
		}
		if (startedDate != null && !"开始时间".equals(startedDate)) {
			sbInner.append("and create_time >= '").append(startedDate).append(" 00:00:00' ");
		}
		if (endDate != null && !"结束时间".equals(endDate)) {
			sbInner.append("and create_time <= '").append(endDate).append(" 23:59:59' ");
		}
		
		if (sbInner.length() > 0) {
			sbInner.replace(0, 3, "where");
		}
		sb.append(sbInner);
//		String sql = "select _id,car_number,is_legal_car,is_yellow_car,is_blacklist_car,is_seized_car,is_checkok_car,is_scrapped_car,legal_number,"
//				+ "img,video,is_reported,create_time from t_carnumber_info "
//				+ " order by _id desc limit ? , ?";
//		Log.i("-xiaomo-",sb.toString() + " ||pageBean.getStart()="+pageBean.getStart()+"\tpageBean.getPageSize()="+pageBean.getPageSize());
		Cursor result =  db.rawQuery(sb.toString(), null);
		result.moveToNext();
		count = result.getInt(0);
		return count;
	}
	
	public CarNumberInfo query(Long _id){
		String sql = "select _id,car_number,is_legal_car,is_yellow_car,is_blacklist_car,is_seized_car,is_checkok_car,is_scrapped_car,legal_number,"
				+ "img,video,is_reported,create_time,compare_time,server_carid,owner,owner_id,"
				+ "maker,type,vin,engine_no,car_color,car_type from t_carnumber_info  where _id = ?";
		String[] args = new String[]{
			String.valueOf(_id)
		};
		Cursor result =  db.rawQuery(sql, args);
		CarNumberInfo car = new CarNumberInfo();
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			car.setCarId(result.getLong(0));
			car.setCarNumber(result.getString(1));
			car.setIsLegalCar(result.getInt(2));
			car.setIsYellowCar(result.getInt(3));
			car.setIsBlackListCar(result.getInt(4));
			car.setIsSeizedCar(result.getInt(5));
			car.setIsCheckOkCar(result.getInt(6));
			car.setIsScrappedCar(result.getInt(7));
			car.setLegalNumber(result.getInt(8));
			car.setImg(result.getString(9));
			car.setVideo(result.getString(10));
			car.setIsReported(result.getInt(11));
			car.setCreateTime(result.getString(12));
			car.setCompareTime(result.getString(13));
			car.setServerCarId(result.getLong(14));
			car.setOwner(result.getString(15));
			car.setOwnerId(result.getString(16));
			car.setMaker(result.getString(17));
			car.setType(result.getString(18));
			car.setVin(result.getString(19));
			car.setEngineNo(result.getString(20));
			car.setCarColor(result.getString(21));
			car.setCarType(result.getString(22));
		}
		Log.i("-xiaomo-", "-----out----"+car);
		return car;
	}
	
	
	public void updateIsReported(Long _id){
		String sql = "update  t_carnumber_info  set is_reported = 1 where  server_carid = ?";
		String[] args = new String[]{
				String.valueOf(_id)
			};
//		try {
			db.execSQL(sql,args);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
	}
	
	public LinkedList<PiePojo> getPieInfo(){
		LinkedList<PiePojo> list = new LinkedList<PiePojo>();
		String sql = "select substr(create_time,1,10), sum(is_legal_car),sum(is_yellow_car),sum(is_blacklist_car),sum(is_seized_car),sum(is_checkok_car),sum(is_scrapped_car) from t_carnumber_info  group by substr(create_time,1,10) order by substr(create_time,1,10) desc limit 0 ,15 ";
		Cursor result =  db.rawQuery(sql, null);
		PiePojo pie = null;
		for (result.moveToFirst(); !result.isAfterLast(); result.moveToNext()) {
			pie = new PiePojo();
			pie.date = result.getString(0);
			pie.blackSum = result.getInt(3);
			pie.totalSum = (result.getInt(1) +result.getInt(2) + result.getInt(3) + result.getInt(4) + result.getInt(5) + result.getInt(6));
			list.add(pie);
		}
		return list;
	}
	
	public LinkedList<Integer> getCarInfoTime(int datBefore){
		String sql = "select sum(is_legal_car),sum(is_yellow_car),sum(is_blacklist_car),sum(is_seized_car),sum(is_checkok_car),sum(is_scrapped_car) from t_carnumber_info  where create_time >= ?";
		 Calendar mycalendar=Calendar.getInstance();
		 SimpleDateFormat dft = new SimpleDateFormat("yyyy-MM-dd 00:00:00",Locale.CHINA);
		 mycalendar.set(Calendar.DATE, mycalendar.get(Calendar.DATE) - datBefore );
		 Log.i("-xiaomo-", (dft.format(mycalendar.getTime())));
		String[] args = new String[]{
				String.valueOf(dft.format(mycalendar.getTime()))
			};
		Cursor result =  db.rawQuery(sql, args);
		LinkedList<Integer> list = new LinkedList<Integer>();
		result.moveToNext();
		list.add(result.getInt(0));
		list.add(result.getInt(1));
		list.add(result.getInt(2));
		list.add(result.getInt(3));
		list.add(result.getInt(4));
		list.add(result.getInt(5));
		return list;
	} 
}
