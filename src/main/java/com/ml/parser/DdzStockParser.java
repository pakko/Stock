package com.ml.parser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.ml.crawler.Crawler;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.model.DdzStock;
import com.ml.util.NumberUtil;

public class DdzStockParser implements Parser<DdzStock> {

	private static final String year = "2013-";
	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	/*
	 * &values=0,0.01,0,0,0.219,0,0,0,0,0,0.078,0,0.028,0,0,0.012,0.215,0.125,0,0.018&
	   &values_2=-0.323,0,-0.085,-0.347,0,-0.253,-0.236,-0.269,-0.112,-0.0040,0,-0.19,0,-0.072,-0.0050,0,0,0,-0.067,0&
	   &x_labels=9-30,10-8,10-9,10-10,10-11,10-14,10-15,10-16,10-17,10-18,10-21,10-22,10-23,10-24,10-25,10-28,10-29,10-30,10-31,11-1&
	 */
	@Override
	public List<DdzStock> parse(String stockCode, String content) {
		if(content.contains("您查询的股票代码或页面不存在"))
			return null;
		try {
			content = content.replaceAll("\r\n", "");
			String inData = content.substring(content.indexOf("values=") + 7, content.indexOf("&&values_2="));
			List<Double> inDatas = transferData(inData);
			String outData = content.substring(content.indexOf("values_2=") + 9, content.indexOf("&&x_labels="));
			List<Double> outDatas = transferData(outData);
			String date = content.substring(content.indexOf("x_labels=") + 9, content.indexOf("&&y_min="));
			List<Long> dates = transferDate(date);
			List<DdzStock> results = new ArrayList<DdzStock>();
			for(int i = 0; i < inDatas.size(); i++) {
				DdzStock ddz = new DdzStock(stockCode, dates.get(i), inDatas.get(i), outDatas.get(i));
				results.add(ddz);
			}
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private List<Double> transferData(String data) {
		if(data.equals(""))
			return null;
		String[] ds = data.split(",");
		List<Double> results = new ArrayList<Double>(ds.length);
		for(String str: ds) {
			results.add(NumberUtil.formatNumber(str));
		}
		return results;
	}
	
	private List<Long> transferDate(String data) throws ParseException {
		if(data.equals(""))
			return null;
		String[] ds = data.split(",");
		List<Long> results = new ArrayList<Long>(ds.length);
		for(String str: ds) {
			Date date = sdf.parse(year + str);
			results.add(date.getTime());
		}
		return results;
	}

	public static void main(String[] args) {  
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "002306";
		Site site = Site.getInstance().setCharset("GBK")
				.setUrl("http://bolelife.hk.pkidc.cn/gudata/ddz-line.asp?code=" + stockCode);
		String content = sd.crawl(site).getContent();
		
		DdzStockParser sp = new DdzStockParser();
		List<DdzStock> scs = sp.parse(stockCode, content);
		System.out.println(scs);
    }
	

}
