package com.ml.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "matchIndex", def = "{'code': -1, 'date': -1}", unique = true, dropDups = true)
})
public class MatchResult {
	@Id
	private String id;
	private String code;
	private long date;
	private long flyDate;
	private String strategy;
	private Double d5;
	private Double d10;
	private Double dnow;
	
	public MatchResult() {}
	public MatchResult(String code, long date, long flyDate, String strategy, Double d5,
			Double d10, Double dnow) {
		super();
		this.code = code;
		this.date = date;
		this.flyDate = flyDate;
		this.strategy = strategy;
		this.d5 = d5;
		this.d10 = d10;
		this.dnow = dnow;
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
	public String getStrategy() {
		return strategy;
	}
	public void setStrategy(String strategy) {
		this.strategy = strategy;
	}
	public Double getD5() {
		return d5;
	}
	public void setD5(Double d5) {
		this.d5 = d5;
	}
	public Double getD10() {
		return d10;
	}
	public void setD10(Double d10) {
		this.d10 = d10;
	}
	public Double getDnow() {
		return dnow;
	}
	public void setDnow(Double dnow) {
		this.dnow = dnow;
	}
	public long getFlyDate() {
		return flyDate;
	}
	public void setFlyDate(long flyDate) {
		this.flyDate = flyDate;
	}
	@Override
	public String toString() {
		return "MatchResult [code=" + code + ", date=" + date + ", flyDate="
				+ flyDate + ", strategy=" + strategy + ", d5=" + d5 + ", d10="
				+ d10 + ", dnow=" + dnow + "]";
	}
	
	
	
	
	
}
