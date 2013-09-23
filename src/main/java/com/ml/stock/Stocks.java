package com.ml.stock;
import java.util.List;


public class Stocks {
	private String stockCode;
	private List<Stock> stocks;
	
	public Stocks() {}
	
	public Stocks(String stockCode, List<Stock> stocks) {
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
	public List<Stock> getStocks() {
		return stocks;
	}
	public void setStocks(List<Stock> stocks) {
		this.stocks = stocks;
	}
	@Override
	public String toString() {
		return "Stocks [stockCode=" + stockCode + ", stocks=" + stocks + "]";
	}
	
	
	
}
