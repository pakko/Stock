package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "stockIndex", def = "{'code': -1, 'date': -1}", unique = true)
})
public class DdzStock {
	private String code;
	private Long date;
	private Double in;		//买入
	private Double out;		//卖出
	public DdzStock(String code, Long date, Double in, Double out) {
		super();
		this.code = code;
		this.date = date;
		this.in = in;
		this.out = out;
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
	public Double getIn() {
		return in;
	}
	public void setIn(Double in) {
		this.in = in;
	}
	public Double getOut() {
		return out;
	}
	public void setOut(Double out) {
		this.out = out;
	}
	@Override
	public String toString() {
		return "DDZStock [code=" + code + ", date=" + date + ", in=" + in
				+ ", out=" + out + "]";
	}
	
	
	
	
}
