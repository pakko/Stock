package com.ml.parser;

import hirondelle.date4j.DateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.ml.crawler.Crawler;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.model.Stock;
import com.ml.util.DateUtil;
import com.ml.util.NumberUtil;

public class StockParser implements Parser<Stock> {

	private boolean isLatest;
	
	public StockParser(boolean isLatest) {
		this.isLatest = isLatest;
	}
	
	@Override
	public List<Stock> parse(String stockCode, String content) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			Map<String, List<List<Object>>> map = objectMapper.readValue(content, 
					new TypeReference<HashMap<String, List<List<Object>>>>(){});
			//System.out.println(map.get("record").size());
			List<List<Object>> datas = map.get("record");
			int size = datas.size();
			if(size <= 0)
				return null;
			if(isLatest) {
				List<Object> data = datas.get(size - 1);
				datas = new ArrayList<List<Object>>(1);
				datas.add(data);
			}
			return transferData(stockCode, datas);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	List<Stock> transferData(String stockCode, List<List<Object>> lists) {
		List<Stock> stocks = new ArrayList<Stock>(lists.size());
		for(List<Object> list: lists) {
			DateTime dateTime = new DateTime((String) list.get(0));
        	long date = DateUtil.getMilliseconds(dateTime);
        	double opening = NumberUtil.formatNumber((String) list.get(1));
        	double max = NumberUtil.formatNumber((String) list.get(2));
        	double close = NumberUtil.formatNumber((String) list.get(3));
        	double min = NumberUtil.formatNumber((String) list.get(4));
        	double tradeVolume = NumberUtil.formatNumber((String) list.get(5));
        	double change = NumberUtil.formatNumber((String) list.get(6));
        	double changeRate = NumberUtil.formatNumber((String) list.get(7));
        	double ma5 = NumberUtil.formatNumber((String) list.get(8));
        	double ma10 = NumberUtil.formatNumber((String) list.get(9));
        	double ma20 = NumberUtil.formatNumber((String) list.get(10));
        	double turnOverRate = 0;
        	if(list.size() > 14)
        		turnOverRate = NumberUtil.formatNumber((String) list.get(14));
        	
			Stock stock = new Stock(stockCode, date, opening, max, close, min, 
					tradeVolume, change, changeRate, ma5, ma10, ma20, turnOverRate);
			stocks.add(stock);
		}
		return stocks;
	}
	

	public static void main(String[] args) {  
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "sh000001";
		Site site = Site.getInstance().setCharset("GBK")
				.setUrl("http://api.finance.ifeng.com/index.php/akdaily/?code=" + stockCode + "&type=last");
		String content = sd.crawl(site).getContent();
		
		StockParser sp = new StockParser(true);
		List<Stock> scs = sp.parse(stockCode, content);
		System.out.println(scs);
    }
	

}
