package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "scenarioIndex", def = "{'code': -1, 'date': -1}", unique = true)
}) 
public class ScenarioResult {
	private String code; //股票代码
	private long date; //日期
	private double price; //当前价格
	private double avgTurnOverRate; //100日平均换手率 
	private double fiveAP;		//5日均价
	private double tenAP; 		//10日均价
	private double twentyAP; 	//20日均价 
	private double totalChangeRate; //100天内涨跌幅度
	private double thirtyAP; //30日均价
	private double sixtyAP; //60日均价
	
	private double avgTurnOverRate_5; //5日平均换手率
	private double avgTurnOverRate_10; //10日平均换手率
	private double avgTurnOverRate_20; //20日平均换手率
	private double avgTurnOverRate_30; //30日平均换手率
	private double avgTurnOverRate_60; //60日平均换手率
	
	public ScenarioResult(String code, long date, double price,
			double avgTurnOverRate, double totalChangeRate, double fiveAP,
			double tenAP, double twentyAP, double thirtyAP,
			double sixtyAP, double avgTurnOverRate_5,
			double avgTurnOverRate_10, double avgTurnOverRate_20,
			double avgTurnOverRate_30, double avgTurnOverRate_60) {
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
		this.sixtyAP = sixtyAP;
		this.avgTurnOverRate_5 = avgTurnOverRate_5;
		this.avgTurnOverRate_10 = avgTurnOverRate_10;
		this.avgTurnOverRate_20 = avgTurnOverRate_20;
		this.avgTurnOverRate_30 = avgTurnOverRate_30;
		this.avgTurnOverRate_60 = avgTurnOverRate_60;
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
	public double getSixtyAP() {
		return sixtyAP;
	}

	public void setSixtyAP(double sixtyAP) {
		this.sixtyAP = sixtyAP;
	}

	public double getAvgTurnOverRate_5() {
		return avgTurnOverRate_5;
	}

	public void setAvgTurnOverRate_5(double avgTurnOverRate_5) {
		this.avgTurnOverRate_5 = avgTurnOverRate_5;
	}

	public double getAvgTurnOverRate_10() {
		return avgTurnOverRate_10;
	}

	public void setAvgTurnOverRate_10(double avgTurnOverRate_10) {
		this.avgTurnOverRate_10 = avgTurnOverRate_10;
	}

	public double getAvgTurnOverRate_20() {
		return avgTurnOverRate_20;
	}

	public void setAvgTurnOverRate_20(double avgTurnOverRate_20) {
		this.avgTurnOverRate_20 = avgTurnOverRate_20;
	}

	public double getAvgTurnOverRate_30() {
		return avgTurnOverRate_30;
	}

	public void setAvgTurnOverRate_30(double avgTurnOverRate_30) {
		this.avgTurnOverRate_30 = avgTurnOverRate_30;
	}

	public double getAvgTurnOverRate_60() {
		return avgTurnOverRate_60;
	}

	public void setAvgTurnOverRate_60(double avgTurnOverRate_60) {
		this.avgTurnOverRate_60 = avgTurnOverRate_60;
	}
	@Override
	public String toString() {
		return "ScenarioResult [code=" + code + ", date=" + date + ", price="
				+ price + ", avgTurnOverRate=" + avgTurnOverRate
				+ ", totalChangeRate=" + totalChangeRate + ", fiveAP=" + fiveAP
				+ ", tenAP=" + tenAP + ", twentyAP=" + twentyAP + ", thirtyAP="
				+ thirtyAP + ", sixtyAP=" + sixtyAP + "]";
	}
	
	
	
}
