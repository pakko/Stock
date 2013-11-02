package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "stockIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class RealStock {
	private String code;
	private Long date;
	private Double now;		//最新价
	private Double close;	//昨日收盘
	private Double change;	//涨跌额
	private Double changeRate;	//涨跌幅
	private Double opening;	//开盘价
	private Double max;		//最高价
	private Double min;		//最低价
	private Double tradeVolume;	//成交量
	private Double tradeAmount;	//成交额
	
	public RealStock(String code, Long date, Double now, Double close,
			Double change, Double changeRate, Double opening, Double max,
			Double min, Double tradeVolume, Double tradeAmount) {
		super();
		this.code = code;
		this.date = date;
		this.now = now;
		this.close = close;
		this.change = change;
		this.changeRate = changeRate;
		this.opening = opening;
		this.max = max;
		this.min = min;
		this.tradeVolume = tradeVolume;
		this.tradeAmount = tradeAmount;
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
	public Double getNow() {
		return now;
	}
	public void setNow(Double now) {
		this.now = now;
	}
	public Double getClose() {
		return close;
	}
	public void setClose(Double close) {
		this.close = close;
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
	public Double getTradeAmount() {
		return tradeAmount;
	}
	public void setTradeAmount(Double tradeAmount) {
		this.tradeAmount = tradeAmount;
	}
	@Override
	public String toString() {
		return "RealStock [code=" + code + ", date=" + date + ", now=" + now
				+ ", close=" + close + ", change=" + change + ", changeRate="
				+ changeRate + ", opening=" + opening + ", max=" + max
				+ ", min=" + min + ", tradeVolume=" + tradeVolume
				+ ", tradeAmount=" + tradeAmount + "]";
	}
	
	
	
}
