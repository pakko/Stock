package com.ml.parser;

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
import com.ml.model.RealStock;
import com.ml.util.NumberUtil;

public class RealStockParser implements Parser<RealStock> {

	/*
	 * var json_q={"sz002306":[0,4.19,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1383289485,1383289487]};
	 */
	@Override
	public List<RealStock> parse(String none, String content) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			content = content.substring(content.indexOf("{"), content.indexOf("}") + 1);
			Map<String, List<Object>> map = objectMapper.readValue(content, 
					new TypeReference<HashMap<String, List<Object>>>(){});
			List<RealStock> results = new ArrayList<RealStock>(map.size());
			for(String key: map.keySet()) {
				RealStock rs = this.transferData(key, map.get(key));
				if(rs == null)
					continue;
				results.add(rs);
			}
			return results;
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private RealStock transferData(String stockCode, List<Object> list) {
		if(list.size() <= 0)
			return null;
		try{
	    	double now = NumberUtil.formatNumber(String.valueOf(list.get(0)));
	    	double close = NumberUtil.formatNumber(String.valueOf(list.get(1)));
	    	double change = NumberUtil.formatNumber(String.valueOf(list.get(2)));
	    	double changeRate = NumberUtil.formatNumber(String.valueOf(list.get(3)));
	    	double opening = NumberUtil.formatNumber(String.valueOf(list.get(4)));
	    	double max = NumberUtil.formatNumber(String.valueOf(list.get(5)));
	    	double min = NumberUtil.formatNumber(String.valueOf(list.get(6)));
	    	double tradeVolume = NumberUtil.formatNumber(String.valueOf(list.get(9)));
	    	double tradeAmount = NumberUtil.formatNumber(String.valueOf(list.get(10)));
	    	long date = Long.parseLong(String.valueOf(list.get(34) + "000"));
	    	
	    	RealStock realStock = new RealStock(stockCode, date, now, close, change, changeRate, 
	    			opening, max, min, tradeVolume, tradeAmount);
			return realStock;
		} catch(Exception e) {
			e.printStackTrace();
			System.out.println(stockCode + ": " + list);
		}
		return null;
	}

	public static void main(String[] args) {  
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "sz002306,sh600000";
		Site site = Site.getInstance().setCharset("GBK")
				.setUrl("http://hq.finance.ifeng.com/q.php?l=" + stockCode + ",&f=json");
		String content = sd.crawl(site).getContent();
		
		RealStockParser sp = new RealStockParser();
		List<RealStock> scs = sp.parse(stockCode, content);
		System.out.println(scs);
    }
	

}
