package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "matchIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class MatchResult {
	private String code;
	private long date;
	
	public MatchResult(String code, long date) {
		super();
		this.code = code;
		this.date = date;
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
	@Override
	public String toString() {
		return "MatchResult [code=" + code + ", date=" + date + "]";
	}
	
	
	
	
}
