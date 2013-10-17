package com.ml.stock;
import java.util.Date;

import org.springframework.data.mongodb.core.index.Indexed;


public class Stock implements Comparable {
	@Indexed(unique = true)
	private Date date;
	private double openSpan;
	private double closeSpan;
	private double upMount;
	private double upBandth;
	private double min;
	private double max;
	private long dealMount;
	private double dealMoney;
	private double changeRate;
	
	public Stock() {
		
	}

	public Stock(Date date, double openSpan, double closeSpan, double upMount,
			double upBandth, double min, double max, long dealMount,
			double dealMoney, double changeRate) {
		super();
		this.date = date;
		this.openSpan = openSpan;
		this.closeSpan = closeSpan;
		this.upMount = upMount;
		this.upBandth = upBandth;
		this.min = min;
		this.max = max;
		this.dealMount = dealMount;
		this.dealMoney = dealMoney;
		this.changeRate = changeRate;
	}

	@Override
	public String toString() {
		return "Stock [date=" + date + ", openSpan=" + openSpan
				+ ", closeSpan=" + closeSpan + ", upMount=" + upMount
				+ ", upBandth=" + upBandth + ", min=" + min + ", max=" + max
				+ ", dealMount=" + dealMount + ", dealMoney=" + dealMoney
				+ ", changeRate=" + changeRate + "]";
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public double getOpenSpan() {
		return openSpan;
	}

	public void setOpenSpan(double openSpan) {
		this.openSpan = openSpan;
	}

	public double getCloseSpan() {
		return closeSpan;
	}

	public void setCloseSpan(double closeSpan) {
		this.closeSpan = closeSpan;
	}

	public double getUpMount() {
		return upMount;
	}

	public void setUpMount(double upMount) {
		this.upMount = upMount;
	}

	public double getUpBandth() {
		return upBandth;
	}

	public void setUpBandth(double upBandth) {
		this.upBandth = upBandth;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public long getDealMount() {
		return dealMount;
	}

	public void setDealMount(long dealMount) {
		this.dealMount = dealMount;
	}

	public double getDealMoney() {
		return dealMoney;
	}

	public void setDealMoney(double dealMoney) {
		this.dealMoney = dealMoney;
	}

	public double getChangeRate() {
		return changeRate;
	}

	public void setChangeRate(double changeRate) {
		this.changeRate = changeRate;
	}

	@Override
	public int compareTo(Object o) {
		Stock stock = (Stock) o;
		return date.getTime() > stock.getDate().getTime() ? 1 : (date.getTime() == stock.getDate().getTime() ? 0 : -1);
	}
	
}
