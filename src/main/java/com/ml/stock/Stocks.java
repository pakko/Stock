package com.ml.stock;
import java.util.Set;

import org.springframework.data.annotation.Id;


public class Stocks {
	@Id
	private String stockCode;
	private Set<Stock> stocks;
	
	public Stocks() {}
	
	public Stocks(String stockCode, Set<Stock> stocks) {
		super();
		this.stockCode = stockCode;
		this.stocks = stocks;
	}
	
	public String getStockCode() {
		return stockCode;
	}
	public void setStockCode(String stockCode) {
		this.stockCode = stockCode;
	}
	public Set<Stock> getStocks() {
		return stocks;
	}
	public void setStocks(Set<Stock> stocks) {
		this.stocks = stocks;
	}
	@Override
	public String toString() {
		return "Stocks [stockCode=" + stockCode + ", stocks=" + stocks + "]";
	}
	
	
	
}
