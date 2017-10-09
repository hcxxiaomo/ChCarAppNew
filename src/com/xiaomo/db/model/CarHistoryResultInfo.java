package com.xiaomo.db.model;

public class CarHistoryResultInfo {

	private Long _id;
	private String img ;
	private String carNumber ;
	private String time ;
	private String compareResult ;
	private String recordFile ;
	private String upload ;
	public String getImg() {
		return img;
	}
	public void setImg(String img) {
		this.img = img;
	}
	public String getCarNumber() {
		return carNumber;
	}
	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getCompareResult() {
		return compareResult;
	}
	public void setCompareResult(String compareResult) {
		this.compareResult = compareResult;
	}
	public String getRecordFile() {
		return recordFile;
	}
	public void setRecordFile(String recordFile) {
		this.recordFile = recordFile;
	}
	public String getUpload() {
		return upload;
	}
	public void setUpload(String upload) {
		this.upload = upload;
	}
	public CarHistoryResultInfo(String img, String carNumber, String time,
			String compareResult, String recordFile, String upload) {
		this.img = img;
		this.carNumber = carNumber;
		this.time = time;
		this.compareResult = compareResult;
		this.recordFile = recordFile;
		this.upload = upload;
	}
	public CarHistoryResultInfo() {
	}
	public Long get_id() {
		return _id;
	}
	public void set_id(Long _id) {
		this._id = _id;
	}
	
	
	
}
