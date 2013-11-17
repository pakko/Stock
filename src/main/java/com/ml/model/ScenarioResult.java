package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "scenarioIndex", def = "{'code': -1, 'date': -1}", unique = true)
}) 
public class ScenarioResult {
	private String code; //股票代码
	private Long date; //日期
	private Double nowPrice;
	private Double ltp;		//流通盘
	
	private Double ma5;		//均价
	private Double ma10;
	private Double ma20;
	private Double ma30;
	private Double ma60;
	private Double ma120;
	private Double ma250;
	
	private Double hsl5;	//换手率
	private Double hsl10;
	private Double hsl20;
	private Double hsl30;
	private Double hsl60;
	private Double hsl120;
	private Double hsl250;
	
	private Double up5;		//上涨幅度
	private Double up10;
	private Double up20;
	private Double up30;
	private Double up60;
	private Double up120;
	private Double up250;
	
	private Boolean isReal;

	public ScenarioResult(String code, Long date, Double nowPrice, Double ltp,
			Double ma5, Double ma10, Double ma20, Double ma30, Double ma60,
			Double ma120, Double ma250, Double hsl5, Double hsl10,
			Double hsl20, Double hsl30, Double hsl60, Double hsl120,
			Double hsl250, Double up5, Double up10, Double up20, Double up30,
			Double up60, Double up120, Double up250, Boolean isReal) {
		super();
		this.code = code;
		this.date = date;
		this.nowPrice = nowPrice;
		this.ltp = ltp;
		this.ma5 = ma5;
		this.ma10 = ma10;
		this.ma20 = ma20;
		this.ma30 = ma30;
		this.ma60 = ma60;
		this.ma120 = ma120;
		this.ma250 = ma250;
		this.hsl5 = hsl5;
		this.hsl10 = hsl10;
		this.hsl20 = hsl20;
		this.hsl30 = hsl30;
		this.hsl60 = hsl60;
		this.hsl120 = hsl120;
		this.hsl250 = hsl250;
		this.up5 = up5;
		this.up10 = up10;
		this.up20 = up20;
		this.up30 = up30;
		this.up60 = up60;
		this.up120 = up120;
		this.up250 = up250;
		this.isReal = isReal;
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

	public Double getNowPrice() {
		return nowPrice;
	}

	public void setNowPrice(Double nowPrice) {
		this.nowPrice = nowPrice;
	}

	public Double getLtp() {
		return ltp;
	}

	public void setLtp(Double ltp) {
		this.ltp = ltp;
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

	public Double getMa30() {
		return ma30;
	}

	public void setMa30(Double ma30) {
		this.ma30 = ma30;
	}

	public Double getMa60() {
		return ma60;
	}

	public void setMa60(Double ma60) {
		this.ma60 = ma60;
	}

	public Double getMa120() {
		return ma120;
	}

	public void setMa120(Double ma120) {
		this.ma120 = ma120;
	}

	public Double getMa250() {
		return ma250;
	}

	public void setMa250(Double ma250) {
		this.ma250 = ma250;
	}

	public Double getHsl5() {
		return hsl5;
	}

	public void setHsl5(Double hsl5) {
		this.hsl5 = hsl5;
	}

	public Double getHsl10() {
		return hsl10;
	}

	public void setHsl10(Double hsl10) {
		this.hsl10 = hsl10;
	}

	public Double getHsl20() {
		return hsl20;
	}

	public void setHsl20(Double hsl20) {
		this.hsl20 = hsl20;
	}

	public Double getHsl30() {
		return hsl30;
	}

	public void setHsl30(Double hsl30) {
		this.hsl30 = hsl30;
	}

	public Double getHsl60() {
		return hsl60;
	}

	public void setHsl60(Double hsl60) {
		this.hsl60 = hsl60;
	}

	public Double getHsl120() {
		return hsl120;
	}

	public void setHsl120(Double hsl120) {
		this.hsl120 = hsl120;
	}

	public Double getHsl250() {
		return hsl250;
	}

	public void setHsl250(Double hsl250) {
		this.hsl250 = hsl250;
	}

	public Double getUp5() {
		return up5;
	}

	public void setUp5(Double up5) {
		this.up5 = up5;
	}

	public Double getUp10() {
		return up10;
	}

	public void setUp10(Double up10) {
		this.up10 = up10;
	}

	public Double getUp20() {
		return up20;
	}

	public void setUp20(Double up20) {
		this.up20 = up20;
	}

	public Double getUp30() {
		return up30;
	}

	public void setUp30(Double up30) {
		this.up30 = up30;
	}

	public Double getUp60() {
		return up60;
	}

	public void setUp60(Double up60) {
		this.up60 = up60;
	}

	public Double getUp120() {
		return up120;
	}

	public void setUp120(Double up120) {
		this.up120 = up120;
	}

	public Double getUp250() {
		return up250;
	}

	public void setUp250(Double up250) {
		this.up250 = up250;
	}

	public Boolean isReal() {
		return isReal;
	}

	public void setReal(Boolean isReal) {
		this.isReal = isReal;
	}

	@Override
	public String toString() {
		return "ScenarioResult [code=" + code + ", date=" + date
				+ ", nowPrice=" + nowPrice + ", ltp=" + ltp + ", ma5=" + ma5
				+ ", ma10=" + ma10 + ", ma20=" + ma20 + ", ma30=" + ma30
				+ ", ma60=" + ma60 + ", ma120=" + ma120 + ", ma250=" + ma250
				+ ", hsl5=" + hsl5 + ", hsl10=" + hsl10 + ", hsl20=" + hsl20
				+ ", hsl30=" + hsl30 + ", hsl60=" + hsl60 + ", hsl120="
				+ hsl120 + ", hsl250=" + hsl250 + ", up5=" + up5 + ", up10="
				+ up10 + ", up20=" + up20 + ", up30=" + up30 + ", up60=" + up60
				+ ", up120=" + up120 + ", up250=" + up250 + ", isReal="
				+ isReal + "]";
	}
	
	
	
	
}
