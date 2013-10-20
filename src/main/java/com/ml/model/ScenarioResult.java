package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "scenarioIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class ScenarioResult {
	private String code;
	private long date;
	private double price;
	private double avgTurnOverRate; //平均换手率
	private double totalChangeRate; //总涨跌幅
	private double fiveAP;		//五日均价
	private double tenAP; 		//十日均价
	private double twentyAP; 	//二十日均价
	private double thirtyAP;	//三十日均价
	
	public ScenarioResult(String code, long date, double price,
			double avgTurnOverRate, double totalChangeRate, double fiveAP,
			double tenAP, double twentyAP, double thirtyAP) {
		super();
		this.code = code;
		this.date = date;
		this.price = price;
		this.avgTurnOverRate = avgTurnOverRate;
		this.totalChangeRate = totalChangeRate;
		this.fiveAP = fiveAP;
		this.tenAP = tenAP;
		this.twentyAP = twentyAP;
		this.thirtyAP = thirtyAP;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public long getDate() {
		return date;
	}
	public void setDate(long date) {
		this.date = date;
	}
	public double getPrice() {
		return price;
	}
	public void setPrice(double price) {
		this.price = price;
	}
	public double getAvgTurnOverRate() {
		return avgTurnOverRate;
	}
	public void setAvgTurnOverRate(double avgTurnOverRate) {
		this.avgTurnOverRate = avgTurnOverRate;
	}
	public double getTotalChangeRate() {
		return totalChangeRate;
	}
	public void setTotalChangeRate(double totalChangeRate) {
		this.totalChangeRate = totalChangeRate;
	}
	public double getFiveAP() {
		return fiveAP;
	}
	public void setFiveAP(double fiveAP) {
		this.fiveAP = fiveAP;
	}
	public double getTenAP() {
		return tenAP;
	}
	public void setTenAP(double tenAP) {
		this.tenAP = tenAP;
	}
	public double getTwentyAP() {
		return twentyAP;
	}
	public void setTwentyAP(double twentyAP) {
		this.twentyAP = twentyAP;
	}
	public double getThirtyAP() {
		return thirtyAP;
	}
	public void setThirtyAP(double thirtyAP) {
		this.thirtyAP = thirtyAP;
	}
	@Override
	public String toString() {
		return "ScenarioResult [code=" + code + ", date=" + date + ", price="
				+ price + ", avgTurnOverRate=" + avgTurnOverRate
				+ ", totalChangeRate=" + totalChangeRate + ", fiveAP=" + fiveAP
				+ ", tenAP=" + tenAP + ", twentyAP=" + twentyAP + ", thirtyAP="
				+ thirtyAP + "]";
	}
	
	
	
}
