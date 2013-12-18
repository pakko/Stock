package com.ml.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import hirondelle.date4j.DateTime;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ml.crawler.Crawler;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.model.ShareHolder;
import com.ml.util.DateUtil;
import com.ml.util.NumberUtil;

public class ShareHolderParser implements Parser<ShareHolder> {
	
	@Override
	public List<ShareHolder> parse(String stockCode, String html) {
		if (html != null && !"".equals(html)) {
            Document doc = Jsoup.parse(html);
            Elements trs = doc.select("graph");
            int size = trs.size();
            if(size <= 0)
            	return null;
            List<ShareHolder> list = new ArrayList<ShareHolder>(size - 1);
            
            for (Element row: trs) {  
            	Elements cols = row.children();
            	ListIterator<Element> colIter = cols.listIterator();
            	while(colIter.hasNext()) {
            		DateTime dateTime = new DateTime(colIter.next().attr("name"));
                	long date = DateUtil.getMilliseconds(dateTime);
            		double totalHolder = NumberUtil.formatNumber(colIter.next().attr("value"));
            		ShareHolder sh = new ShareHolder(stockCode, date, totalHolder, 0);
            		list.add(sh);
            	}
            }
            return list;
        }
		return null;
	}
	
	public static void main(String[] args) {  
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "002306";
		Site site = Site.getInstance().setCharset("GBK")
				.setUrl("http://stock.jrj.com.cn/share/" + stockCode + "/data/capital_gdhs_gdhsbhqs.xml");
		String html = sd.crawl(site).getContent();
		
		ShareHolderParser jp = new ShareHolderParser();
		List<ShareHolder> scs = jp.parse(stockCode, html);
		System.out.println(scs);
    }

	
}
