package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "stockIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class Stock implements Comparable<Stock> {
	private String code;
	private long date;
	private double opening;
	private double close;
	private double change;
	private double changeRate;
	private double min;
	private double max;
	private long tradeVolume;
	private double tradeAmount;
	private double turnOverRate;
	
	

	
	public Stock(String code, long date, double opening, double close,
			double change, double changeRate, double min, double max,
			long tradeVolume, double tradeAmount, double turnOverRate) {
		super();
		this.code = code;
		this.date = date;
		this.opening = opening;
		this.close = close;
		this.change = change;
		this.changeRate = changeRate;
		this.min = min;
		this.max = max;
		this.tradeVolume = tradeVolume;
		this.tradeAmount = tradeAmount;
		this.turnOverRate = turnOverRate;
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


	public double getOpening() {
		return opening;
	}


	public void setOpening(double opening) {
		this.opening = opening;
	}


	public double getClose() {
		return close;
	}


	public void setClose(double close) {
		this.close = close;
	}


	public double getChange() {
		return change;
	}


	public void setChange(double change) {
		this.change = change;
	}


	public double getChangeRate() {
		return changeRate;
	}


	public void setChangeRate(double changeRate) {
		this.changeRate = changeRate;
	}


	public double getMin() {
		return min;
	}


	public void setMin(double min) {
		this.min = min;
	}


	public double getMax() {
		return max;
	}


	public void setMax(double max) {
		this.max = max;
	}


	public long getTradeVolume() {
		return tradeVolume;
	}


	public void setTradeVolume(long tradeVolume) {
		this.tradeVolume = tradeVolume;
	}


	public double getTradeAmount() {
		return tradeAmount;
	}


	public void setTradeAmount(double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}


	public double getTurnOverRate() {
		return turnOverRate;
	}


	public void setTurnOverRate(double turnOverRate) {
		this.turnOverRate = turnOverRate;
	}


	@Override
	public String toString() {
		return "Stock [code=" + code + ", date=" + date + ", opening="
				+ opening + ", close=" + close + ", change=" + change
				+ ", changeRate=" + changeRate + ", min=" + min + ", max="
				+ max + ", tradeVolume=" + tradeVolume + ", tradeAmount="
				+ tradeAmount + ", turnOverRate=" + turnOverRate + "]";
	}


	@Override
	public int compareTo(Stock stock) {
		return date > stock.getDate() ? -1 : (date == stock.getDate() ? 0 : 1);
	}
	
}
