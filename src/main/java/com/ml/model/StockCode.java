package com.ml.model;

import org.springframework.data.mongodb.core.index.Indexed;

public class StockCode {
	@Indexed(unique = true)
	private String code;
	private String name;
	
	public StockCode(String code, String name) {
		super();
		this.code = code;
		this.name = name;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "StockCode [code=" + code + ", name=" + name + "]";
	}
	
	
	
}
