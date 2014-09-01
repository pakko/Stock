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
	private Double d20;
	private Double d30;
	private Double dnow;
	
	public MatchResult() {}

	public MatchResult(String code, long date, long flyDate,
			String strategy, Double d5, Double d10, Double d20, Double d30,
			Double dnow) {
		super();
		this.code = code;
		this.date = date;
		this.flyDate = flyDate;
		this.strategy = strategy;
		this.d5 = d5;
		this.d10 = d10;
		this.d20 = d20;
		this.d30 = d30;
		this.dnow = dnow;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public long getFlyDate() {
		return flyDate;
	}

	public void setFlyDate(long flyDate) {
		this.flyDate = flyDate;
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

	public Double getD20() {
		return d20;
	}

	public void setD20(Double d20) {
		this.d20 = d20;
	}

	public Double getD30() {
		return d30;
	}

	public void setD30(Double d30) {
		this.d30 = d30;
	}

	public Double getDnow() {
		return dnow;
	}

	public void setDnow(Double dnow) {
		this.dnow = dnow;
	}

	@Override
	public String toString() {
		return "MatchResult [id=" + id + ", code=" + code + ", date=" + date
				+ ", flyDate=" + flyDate + ", strategy=" + strategy + ", d5="
				+ d5 + ", d10=" + d10 + ", d20=" + d20 + ", d30=" + d30
				+ ", dnow=" + dnow + "]";
	}
	
	
	
	
	
}
