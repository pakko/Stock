package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "shareHolder", def = "{'code': -1, 'date': -1}", unique = true)
}) 
public class ShareHolder implements Comparable<ShareHolder> {
	private String code;
	private long date;
	private double totalHolders;	//总股东人数
	private double avgShare;	//人均持股数
	
	
	public ShareHolder(String code, long date, double totalHolders,
			double avgShare) {
		super();
		this.code = code;
		this.date = date;
		this.totalHolders = totalHolders;
		this.avgShare = avgShare;
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
	public double getTotalHolders() {
		return totalHolders;
	}
	public void setTotalHolders(double totalHolders) {
		this.totalHolders = totalHolders;
	}
	public double getAvgShare() {
		return avgShare;
	}
	public void setAvgShare(double avgShare) {
		this.avgShare = avgShare;
	}
	@Override
	public String toString() {
		return "ShareHolder [code=" + code + ", date=" + date
				+ ", totalHolders=" + totalHolders + ", avgShare=" + avgShare
				+ "]";
	}
	
	@Override
	public int compareTo(ShareHolder sh) {
		return sh.getDate() > date ? 1 : -1; 
	}
	
}
