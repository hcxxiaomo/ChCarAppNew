package com.xiaomo.db.model;

public class CarNumberRaidInfo {
	
	public Integer id;
	public String carNumber;
	public String illegalString;
	public String createdTime;
	public String imgPath;
	public String isReported;
	public CarNumberRaidInfo(Integer id, String carNumber, String illegalString,
			String createdTime, String imgPath, String isReported) {
		super();
		this.id = id;
		this.carNumber = carNumber;
		this.illegalString = illegalString;
		this.createdTime = createdTime;
		this.imgPath = imgPath;
		this.isReported = isReported;
	}
	public CarNumberRaidInfo() {
	}

}
