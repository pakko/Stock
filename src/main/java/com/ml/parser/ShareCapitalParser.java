package com.ml.parser;

import java.util.ArrayList;
import java.util.List;

import hirondelle.date4j.DateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ml.crawler.Crawler;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.model.ShareCapital;
import com.ml.util.DateUtil;
import com.ml.util.NumberUtil;

public class ShareCapitalParser implements Parser<ShareCapital> {
	
	@Override
	public List<ShareCapital> parse(String stockCode, String html) {
		if (html != null && !"".equals(html)) {
            Document doc = Jsoup.parse(html);
            Elements trs = doc.select("table.tab5>tbody>tr");
            int size = trs.size();
            if(size <= 0)
            	return null;
            List<ShareCapital> list = new ArrayList<ShareCapital>(size - 1);
            for (Element row: trs) {  
            	Elements cols = row.children();
            	if(cols.first().tagName().equals("th"))
            		continue;
            	DateTime dateTime = new DateTime(cols.get(0).html().replaceAll("&nbsp;", ""));
            	long date = DateUtil.getMilliseconds(dateTime);
            	double totalShare = NumberUtil.formatNumber(cols.get(1).html().replace("&nbsp;", ""));
            	double tradableShare = NumberUtil.formatNumber(cols.get(3).html().replace("&nbsp;", ""));

            	ShareCapital sc = new ShareCapital(stockCode, date, totalShare, tradableShare);
            	list.add(sc);
            }
            return list;
        }
		return null;
	}
	
	public static void main(String[] args) {  
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "002306";
		Site site = Site.getInstance().setCharset("GBK")
				.setUrl("http://stock.jrj.com.cn/share," + stockCode + ",gbjg.shtml");
		String html = sd.crawl(site).getContent();
		
		ShareCapitalParser jp = new ShareCapitalParser();
		List<ShareCapital> scs = jp.parse(stockCode, html);
		System.out.println(scs);
    }

	
}
