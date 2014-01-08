package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "ddxIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class DdxStock {
	private String code;
	private Long date;
	private Double ddx;		
	public DdxStock(String code, Long date, Double ddx) {
		super();
		this.code = code;
		this.date = date;
		this.ddx = ddx;
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
	public Double getDdx() {
		return ddx;
	}
	public void setDdx(Double ddx) {
		this.ddx = ddx;
	}
	
	
	
}
