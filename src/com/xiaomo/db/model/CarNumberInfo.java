package com.xiaomo.db.model;

import java.util.Date;

public class CarNumberInfo {
	
	private Long carId;
    private String carNumber;
	
	private String color;
	
    private String maker;
	
    private String type;
	
	private String vin ; 

	private String engineNo;
	
	private Integer legalNumber  = 0; 
	
	private Integer isLegalCar  = 0; 
	
	private Integer isYellowCar  = 0;
	
	private Integer isBlackListCar  = 0;
	
	private Integer isSeizedCar  = 0; 
	
	private Integer isCheckOkCar  = 0; 
	
	private Integer isScrappedCar  = 0; 
	
	private String owner ;
	
	private String ownerId ;
	
	private String carColor ; 

	private String img ; 
	
	private String video ; 
	
	private Integer isReported  = 0;
	
	private Long serverCarId;
	
    private String createTime;
	
    private String compareTime;
    
    private String carType;
    

	public Long getCarId() {
		return carId;
	}

	public void setCarId(Long carId) {
		this.carId = carId;
	}

	public String getCarNumber() {
		return carNumber;
	}

	public void setCarNumber(String carNumber) {
		this.carNumber = carNumber;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getMaker() {
		return maker;
	}

	public void setMaker(String maker) {
		this.maker = maker;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getVin() {
		return vin;
	}

	public void setVin(String vin) {
		this.vin = vin;
	}

	public String getEngineNo() {
		return engineNo;
	}

	public void setEngineNo(String engineNo) {
		this.engineNo = engineNo;
	}

	public Integer getLegalNumber() {
		return legalNumber;
	}

	public void setLegalNumber(Integer legalNumber) {
		this.legalNumber = legalNumber;
	}

	public Integer getIsYellowCar() {
		return isYellowCar;
	}

	public void setIsYellowCar(Integer isYellowCar) {
		this.isYellowCar = isYellowCar;
	}

	public Integer getIsBlackListCar() {
		return isBlackListCar;
	}

	public void setIsBlackListCar(Integer isBlackListCar) {
		this.isBlackListCar = isBlackListCar;
	}

	public Integer getIsSeizedCar() {
		return isSeizedCar;
	}

	public void setIsSeizedCar(Integer isSeizedCar) {
		this.isSeizedCar = isSeizedCar;
	}

	public Integer getIsCheckOkCar() {
		return isCheckOkCar;
	}

	public void setIsCheckOkCar(Integer isCheckOkCar) {
		this.isCheckOkCar = isCheckOkCar;
	}

	public Integer getIsScrappedCar() {
		return isScrappedCar;
	}

	public void setIsScrappedCar(Integer isScrappedCar) {
		this.isScrappedCar = isScrappedCar;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}

	public String getCarColor() {
		return carColor;
	}

	public void setCarColor(String carColor) {
		this.carColor = carColor;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}


	public Integer getIsReported() {
		return isReported;
	}

	public void setIsReported(Integer isReported) {
		this.isReported = isReported;
	}


	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public void setCompareTime(String compareTime) {
		this.compareTime = compareTime;
	}

	
	

	public String getCreateTime() {
		return createTime;
	}

	public String getCompareTime() {
		return compareTime;
	}

	public Integer getIsLegalCar() {
		return isLegalCar;
	}

	public void setIsLegalCar(Integer isLegalCar) {
		this.isLegalCar = isLegalCar;
	}
	
	public Long getServerCarId() {
		return serverCarId;
	}

	public void setServerCarId(Long serverCarId) {
		this.serverCarId = serverCarId;
	}

	
	
	public String getCarType() {
		return carType;
	}

	public void setCarType(String carType) {
		this.carType = carType;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CarNumberInfo [carId=").append(carId)
				.append(", carNumber=").append(carNumber).append(", color=")
				.append(color).append(", maker=").append(maker)
				.append(", type=").append(type).append(", vin=").append(vin)
				.append(", engineNo=").append(engineNo)
				.append(", legalNumber=").append(legalNumber)
				.append(", isLegalCar=").append(isLegalCar)
				.append(", isYellowCar=").append(isYellowCar)
				.append(", isBlackListCar=").append(isBlackListCar)
				.append(", isSeizedCar=").append(isSeizedCar)
				.append(", isCheckOkCar=").append(isCheckOkCar)
				.append(", isScrappedCar=").append(isScrappedCar)
				.append(", owner=").append(owner).append(", ownerId=")
				.append(ownerId).append(", carColor=").append(carColor)
				.append(", img=").append(img).append(", video=").append(video)
				.append(", isReported=").append(isReported)
				.append(", carType=").append(carType)
				.append(", serverCarId=").append(serverCarId)
				.append(", createTime=").append(createTime)
				.append(", compareTime=").append(compareTime).append("]");
		return builder.toString();
	}

	
	
	
	

}
