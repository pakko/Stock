package com.ml.regression;

import org.springframework.data.annotation.Id;

public class House {
	@Id
	private String id;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	private String name;
	private String url;
	private Double squarFeet;
	private Double totalPrice;
	private Integer layout;
	private Integer floor;
	private Integer totalFloor;
	private Double avgPrice;
	public House(String name, String url, Double squarFeet, Double totalPrice,
			Integer layout, Integer floor, Integer totalFloor, Double avgPrice) {
		super();
		this.name = name;
		this.url = url;
		this.squarFeet = squarFeet;
		this.totalPrice = totalPrice;
		this.layout = layout;
		this.floor = floor;
		this.totalFloor = totalFloor;
		this.avgPrice = avgPrice;
	}
	public House(){}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Double getSquarFeet() {
		return squarFeet;
	}
	public void setSquarFeet(Double squarFeet) {
		this.squarFeet = squarFeet;
	}
	public Double getTotalPrice() {
		return totalPrice;
	}
	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}
	public Integer getLayout() {
		return layout;
	}
	public void setLayout(Integer layout) {
		this.layout = layout;
	}
	public Integer getFloor() {
		return floor;
	}
	public void setFloor(Integer floor) {
		this.floor = floor;
	}
	public Integer getTotalFloor() {
		return totalFloor;
	}
	public void setTotalFloor(Integer totalFloor) {
		this.totalFloor = totalFloor;
	}
	public Double getAvgPrice() {
		return avgPrice;
	}
	public void setAvgPrice(Double avgPrice) {
		this.avgPrice = avgPrice;
	}
	@Override
	public String toString() {
		return "House [name=" + name + ", url=" + url + ", squarFeet="
				+ squarFeet + ", totalPrice=" + totalPrice + ", layout="
				+ layout + ", floor=" + floor + ", totalFloor=" + totalFloor
				+ ", avgPrice=" + avgPrice + "]";
	}
	
	
}
