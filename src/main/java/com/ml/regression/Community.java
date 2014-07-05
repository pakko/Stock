package com.ml.regression;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

public class Community {

	@Id
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String communityUrl;
	private String houseUrl;
	@Indexed(unique=true)
	private String name;
	private List<House> houses;
	private Integer number;
	private String majorLocation;
	private String minorLocation;
	private Double avgPrice;
	private Double upRate;
	private Integer date;
	
	public Community(String communityUrl, String houseUrl, String name,
			List<House> houses, Integer number, String majorLocation,
			String minorLocation, Double avgPrice, Double upRate, Integer date) {
		super();
		this.communityUrl = communityUrl;
		this.houseUrl = houseUrl;
		this.name = name;
		this.houses = houses;
		this.number = number;
		this.majorLocation = majorLocation;
		this.minorLocation = minorLocation;
		this.avgPrice = avgPrice;
		this.upRate = upRate;
		this.date = date;
	}
	
	public Community(){}
	
	public String getCommunityUrl() {
		return communityUrl;
	}
	public void setCommunityUrl(String communityUrl) {
		this.communityUrl = communityUrl;
	}
	public String getHouseUrl() {
		return houseUrl;
	}
	public void setHouseUrl(String houseUrl) {
		this.houseUrl = houseUrl;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<House> getHouses() {
		return houses;
	}
	public void setHouses(List<House> houses) {
		this.houses = houses;
	}
	public Integer getNumber() {
		return number;
	}
	public void setNumber(Integer number) {
		this.number = number;
	}
	public String getMajorLocation() {
		return majorLocation;
	}
	public void setMajorLocation(String majorLocation) {
		this.majorLocation = majorLocation;
	}
	public String getMinorLocation() {
		return minorLocation;
	}
	public void setMinorLocation(String minorLocation) {
		this.minorLocation = minorLocation;
	}
	public Double getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}
	public Double getUpRate() {
		return upRate;
	}
	public void setUpRate(Double upRate) {
		this.upRate = upRate;
	}
	public Integer getDate() {
		return date;
	}
	public void setDate(Integer date) {
		this.date = date;
	}
	@Override
	public String toString() {
		return "Community [communityUrl=" + communityUrl + ", houseUrl="
				+ houseUrl + ", name=" + name + ", houses=" + houses
				+ ", number=" + number + ", majorLocation=" + majorLocation
				+ ", minorLocation=" + minorLocation + ", avgPrice=" + avgPrice
				+ ", upRate=" + upRate + ", date=" + date + "]";
	}
	
	
	
}
