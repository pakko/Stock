package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "stockIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class Stock {
	private String code;
	private Long date;
	private Double opening;
	private Double max;
	private Double close;
	private Double min;
	private Double tradeVolume;
	private Double change;
	private Double changeRate;
	private Double ma5;
	private Double ma10;
	private Double ma20;
	private Double turnOverRate;
	public Stock(String code, Long date, Double opening, Double max,
			Double close, Double min, Double tradeVolume, Double change,
			Double changeRate, Double ma5, Double ma10, Double ma20,
			Double turnOverRate) {
		super();
		this.code = code;
		this.date = date;
		this.opening = opening;
		this.max = max;
		this.close = close;
		this.min = min;
		this.tradeVolume = tradeVolume;
		this.change = change;
		this.changeRate = changeRate;
		this.ma5 = ma5;
		this.ma10 = ma10;
		this.ma20 = ma20;
		this.turnOverRate = turnOverRate;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public Long getDate() {
		return date;
	}
	public void setDate(Long date) {
		this.date = date;
	}
	public Double getOpening() {
		return opening;
	}
	public void setOpening(Double opening) {
		this.opening = opening;
	}
	public Double getMax() {
		return max;
	}
	public void setMax(Double max) {
		this.max = max;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
	}
	public Double getMin() {
		return min;
	}
	public void setMin(Double min) {
		this.min = min;
	}
	public Double getTradeVolume() {
		return tradeVolume;
	}
	public void setTradeVolume(Double tradeVolume) {
		this.tradeVolume = tradeVolume;
	}
	public Double getChange() {
		return change;
	}
	public void setChange(Double change) {
		this.change = change;
	}
	public Double getChangeRate() {
		return changeRate;
	}
	public void setChangeRate(Double changeRate) {
		this.changeRate = changeRate;
	}
	public Double getMa5() {
		return ma5;
	}
	public void setMa5(Double ma5) {
		this.ma5 = ma5;
	}
	public Double getMa10() {
		return ma10;
	}
	public void setMa10(Double ma10) {
		this.ma10 = ma10;
	}
	public Double getMa20() {
		return ma20;
	}
	public void setMa20(Double ma20) {
		this.ma20 = ma20;
	}
	public Double getTurnOverRate() {
		return turnOverRate;
	}
	public void setTurnOverRate(Double turnOverRate) {
		this.turnOverRate = turnOverRate;
	}
	@Override
	public String toString() {
		return "Stock [code=" + code + ", date=" + date + ", opening="
				+ opening + ", max=" + max + ", close=" + close + ", min="
				+ min + ", tradeVolume=" + tradeVolume + ", change=" + change
				+ ", changeRate=" + changeRate + ", ma5=" + ma5 + ", ma10="
				+ ma10 + ", ma20=" + ma20 + ", turnOverRate=" + turnOverRate
				+ "]";
	}
	
	
	
}
