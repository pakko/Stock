package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "shareCapitalIndex", def = "{'code': -1, 'date': -1}", unique = true)
}) 
public class ShareCapital {
	private String code;
	private long date;
	private double totalShare;	//总股本
	private double tradableShare;	//流通股
	
	public ShareCapital(String code, long date, double totalShare,
			double tradableShare) {
		super();
		this.code = code;
		this.date = date;
		this.totalShare = totalShare;
		this.tradableShare = tradableShare;
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


	public double getTotalShare() {
		return totalShare;
	}


	public void setTotalShare(double totalShare) {
		this.totalShare = totalShare;
	}


	public double getTradableShare() {
		return tradableShare;
	}


	public void setTradableShare(double tradableShare) {
		this.tradableShare = tradableShare;
	}


	@Override
	public String toString() {
		return "ShareCapital [code=" + code + ", date=" + date
				+ ", totalShare=" + totalShare + ", tradableShare="
				+ tradableShare + "]";
	}
	
	
	
}
