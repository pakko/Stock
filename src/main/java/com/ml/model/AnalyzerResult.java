package com.ml.model;

import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@CompoundIndexes({
    @CompoundIndex(name = "analyzerIndex", def = "{'code': -1, 'date': -1}", unique = true, dropDups = true)
})
public class AnalyzerResult {
	private String code;
	private long date;				//选出时间
	private String strategy;
	private double hslCurrent5;		//当前到5天前的平均换手率
	private double hslBefore5;		//5天前到其前5天的平均换手率
	private double hslCurrent10;	
	private double hslBefore10;		
	private double hslCurrent20;	
	private double hslBefore20;		
	private double hslCurrent30;	
	private double hslBefore30;		
	private double hslCurrent60;	
	private double hslBefore60;
	private double hslCurrent120;	
	private double hslBefore120;	
	private double hslCurrent250;	
	private double hslBefore250;	
	private double hslCmp5;		//当前5天换手率与前5天的比值hslCurrent5/hslBefore5
	private double hslCmp10;
	private double hslCmp20;
	private double hslCmp30;
	private double hslCmp60;
	private double hslCmp120;
	private double hslCmp250;
	private boolean isAvgPriceGood;	//是否now>5>10>20>30均价
	private double p5ToAvg;			//5日均价与基准均价的差率（基数为4个均价的均值）
	private double p10ToAvg;
	private double p20ToAvg;
	private double p30ToAvg;
	private double pToD5Grad;		//当前价格与前5日的斜率
	private double pToD10Grad;
	private double pToD20Grad;
	private double pToD30Grad;
	private boolean isGradUp;		//这5日斜率是否渐次上升
	private int ltszArea;		//流通市值区间
	private double pe;				//动态市盈率
	private int priceArea;			//股价区间
	private double gapBetSZ;		//与大盘走势差别
	
	public AnalyzerResult() {}

	public AnalyzerResult(String code, long date, String strategy,
			double hslCurrent5, double hslBefore5,
			double hslCurrent10, double hslBefore10, double hslCurrent20,
			double hslBefore20, double hslCurrent30, double hslBefore30,
			double hslCurrent60, double hslBefore60, double hslCurrent120,
			double hslBefore120, double hslCurrent250, double hslBefore250,
			double hslCmp5, double hslCmp10, double hslCmp20, double hslCmp30,
			double hslCmp60, double hslCmp120, double hslCmp250,
			boolean isAvgPriceGood, double p5ToAvg, double p10ToAvg,
			double p20ToAvg, double p30ToAvg, double pToD5Grad,
			double pToD10Grad, double pToD20Grad, double pToD30Grad,
			boolean isGradUp, int ltszArea, double pe, int priceArea,
			double gapBetSZ) {
		super();
		this.code = code;
		this.date = date;
		this.strategy = strategy;
		this.hslCurrent5 = hslCurrent5;
		this.hslBefore5 = hslBefore5;
		this.hslCurrent10 = hslCurrent10;
		this.hslBefore10 = hslBefore10;
		this.hslCurrent20 = hslCurrent20;
		this.hslBefore20 = hslBefore20;
		this.hslCurrent30 = hslCurrent30;
		this.hslBefore30 = hslBefore30;
		this.hslCurrent60 = hslCurrent60;
		this.hslBefore60 = hslBefore60;
		this.hslCurrent120 = hslCurrent120;
		this.hslBefore120 = hslBefore120;
		this.hslCurrent250 = hslCurrent250;
		this.hslBefore250 = hslBefore250;
		this.hslCmp5 = hslCmp5;
		this.hslCmp10 = hslCmp10;
		this.hslCmp20 = hslCmp20;
		this.hslCmp30 = hslCmp30;
		this.hslCmp60 = hslCmp60;
		this.hslCmp120 = hslCmp120;
		this.hslCmp250 = hslCmp250;
		this.isAvgPriceGood = isAvgPriceGood;
		this.p5ToAvg = p5ToAvg;
		this.p10ToAvg = p10ToAvg;
		this.p20ToAvg = p20ToAvg;
		this.p30ToAvg = p30ToAvg;
		this.pToD5Grad = pToD5Grad;
		this.pToD10Grad = pToD10Grad;
		this.pToD20Grad = pToD20Grad;
		this.pToD30Grad = pToD30Grad;
		this.isGradUp = isGradUp;
		this.ltszArea = ltszArea;
		this.pe = pe;
		this.priceArea = priceArea;
		this.gapBetSZ = gapBetSZ;
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

	public double getHslCurrent5() {
		return hslCurrent5;
	}

	public void setHslCurrent5(double hslCurrent5) {
		this.hslCurrent5 = hslCurrent5;
	}

	public double getHslBefore5() {
		return hslBefore5;
	}

	public void setHslBefore5(double hslBefore5) {
		this.hslBefore5 = hslBefore5;
	}

	public double getHslCurrent10() {
		return hslCurrent10;
	}

	public void setHslCurrent10(double hslCurrent10) {
		this.hslCurrent10 = hslCurrent10;
	}

	public double getHslBefore10() {
		return hslBefore10;
	}

	public void setHslBefore10(double hslBefore10) {
		this.hslBefore10 = hslBefore10;
	}

	public double getHslCurrent20() {
		return hslCurrent20;
	}

	public void setHslCurrent20(double hslCurrent20) {
		this.hslCurrent20 = hslCurrent20;
	}

	public double getHslBefore20() {
		return hslBefore20;
	}

	public void setHslBefore20(double hslBefore20) {
		this.hslBefore20 = hslBefore20;
	}

	public double getHslCurrent30() {
		return hslCurrent30;
	}

	public void setHslCurrent30(double hslCurrent30) {
		this.hslCurrent30 = hslCurrent30;
	}

	public double getHslBefore30() {
		return hslBefore30;
	}

	public void setHslBefore30(double hslBefore30) {
		this.hslBefore30 = hslBefore30;
	}

	public double getHslCurrent60() {
		return hslCurrent60;
	}

	public void setHslCurrent60(double hslCurrent60) {
		this.hslCurrent60 = hslCurrent60;
	}

	public double getHslBefore60() {
		return hslBefore60;
	}

	public void setHslBefore60(double hslBefore60) {
		this.hslBefore60 = hslBefore60;
	}

	public double getHslCurrent120() {
		return hslCurrent120;
	}

	public void setHslCurrent120(double hslCurrent120) {
		this.hslCurrent120 = hslCurrent120;
	}

	public double getHslBefore120() {
		return hslBefore120;
	}

	public void setHslBefore120(double hslBefore120) {
		this.hslBefore120 = hslBefore120;
	}

	public double getHslCurrent250() {
		return hslCurrent250;
	}

	public void setHslCurrent250(double hslCurrent250) {
		this.hslCurrent250 = hslCurrent250;
	}

	public double getHslBefore250() {
		return hslBefore250;
	}

	public void setHslBefore250(double hslBefore250) {
		this.hslBefore250 = hslBefore250;
	}

	public double getHslCmp5() {
		return hslCmp5;
	}

	public void setHslCmp5(double hslCmp5) {
		this.hslCmp5 = hslCmp5;
	}

	public double getHslCmp10() {
		return hslCmp10;
	}

	public void setHslCmp10(double hslCmp10) {
		this.hslCmp10 = hslCmp10;
	}

	public double getHslCmp20() {
		return hslCmp20;
	}

	public void setHslCmp20(double hslCmp20) {
		this.hslCmp20 = hslCmp20;
	}

	public double getHslCmp30() {
		return hslCmp30;
	}

	public void setHslCmp30(double hslCmp30) {
		this.hslCmp30 = hslCmp30;
	}

	public double getHslCmp60() {
		return hslCmp60;
	}

	public void setHslCmp60(double hslCmp60) {
		this.hslCmp60 = hslCmp60;
	}

	public double getHslCmp120() {
		return hslCmp120;
	}

	public void setHslCmp120(double hslCmp120) {
		this.hslCmp120 = hslCmp120;
	}

	public double getHslCmp250() {
		return hslCmp250;
	}

	public void setHslCmp250(double hslCmp250) {
		this.hslCmp250 = hslCmp250;
	}

	public boolean isAvgPriceGood() {
		return isAvgPriceGood;
	}

	public void setAvgPriceGood(boolean isAvgPriceGood) {
		this.isAvgPriceGood = isAvgPriceGood;
	}

	public double getP5ToAvg() {
		return p5ToAvg;
	}

	public void setP5ToAvg(double p5ToAvg) {
		this.p5ToAvg = p5ToAvg;
	}

	public double getP10ToAvg() {
		return p10ToAvg;
	}

	public void setP10ToAvg(double p10ToAvg) {
		this.p10ToAvg = p10ToAvg;
	}

	public double getP20ToAvg() {
		return p20ToAvg;
	}

	public void setP20ToAvg(double p20ToAvg) {
		this.p20ToAvg = p20ToAvg;
	}

	public double getP30ToAvg() {
		return p30ToAvg;
	}

	public void setP30ToAvg(double p30ToAvg) {
		this.p30ToAvg = p30ToAvg;
	}

	public double getpToD5Grad() {
		return pToD5Grad;
	}

	public void setpToD5Grad(double pToD5Grad) {
		this.pToD5Grad = pToD5Grad;
	}

	public double getpToD10Grad() {
		return pToD10Grad;
	}

	public void setpToD10Grad(double pToD10Grad) {
		this.pToD10Grad = pToD10Grad;
	}

	public double getpToD20Grad() {
		return pToD20Grad;
	}

	public void setpToD20Grad(double pToD20Grad) {
		this.pToD20Grad = pToD20Grad;
	}

	public double getpToD30Grad() {
		return pToD30Grad;
	}

	public void setpToD30Grad(double pToD30Grad) {
		this.pToD30Grad = pToD30Grad;
	}

	public boolean isGradUp() {
		return isGradUp;
	}

	public void setGradUp(boolean isGradUp) {
		this.isGradUp = isGradUp;
	}

	public int getLtszArea() {
		return ltszArea;
	}

	public void setLtszArea(int ltszArea) {
		this.ltszArea = ltszArea;
	}

	public double getPe() {
		return pe;
	}

	public void setPe(double pe) {
		this.pe = pe;
	}

	public int getPriceArea() {
		return priceArea;
	}

	public void setPriceArea(int priceArea) {
		this.priceArea = priceArea;
	}

	public double getGapBetSZ() {
		return gapBetSZ;
	}

	public void setGapBetSZ(double gapBetSZ) {
		this.gapBetSZ = gapBetSZ;
	}

	@Override
	public String toString() {
		return "AnalyzerResult [code=" + code + ", date=" + date
				+ ", strategy=" + strategy
				+ ", hslCurrent5=" + hslCurrent5 + ", hslBefore5=" + hslBefore5
				+ ", hslCurrent10=" + hslCurrent10 + ", hslBefore10="
				+ hslBefore10 + ", hslCurrent20=" + hslCurrent20
				+ ", hslBefore20=" + hslBefore20 + ", hslCurrent30="
				+ hslCurrent30 + ", hslBefore30=" + hslBefore30
				+ ", hslCurrent60=" + hslCurrent60 + ", hslBefore60="
				+ hslBefore60 + ", hslCurrent120=" + hslCurrent120
				+ ", hslBefore120=" + hslBefore120 + ", hslCurrent250="
				+ hslCurrent250 + ", hslBefore250=" + hslBefore250
				+ ", hslCmp5=" + hslCmp5 + ", hslCmp10=" + hslCmp10
				+ ", hslCmp20=" + hslCmp20 + ", hslCmp30=" + hslCmp30
				+ ", hslCmp60=" + hslCmp60 + ", hslCmp120=" + hslCmp120
				+ ", hslCmp250=" + hslCmp250 + ", isAvgPriceGood="
				+ isAvgPriceGood + ", p5ToAvg=" + p5ToAvg + ", p10ToAvg="
				+ p10ToAvg + ", p20ToAvg=" + p20ToAvg + ", p30ToAvg="
				+ p30ToAvg + ", pToD5Grad=" + pToD5Grad + ", pToD10Grad="
				+ pToD10Grad + ", pToD20Grad=" + pToD20Grad + ", pToD30Grad="
				+ pToD30Grad + ", isGradUp=" + isGradUp + ", ltszArea="
				+ ltszArea + ", pe=" + pe + ", priceArea=" + priceArea
				+ ", gapBetSZ=" + gapBetSZ + "]";
	}
	
	
}
