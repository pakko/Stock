package com.ml.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class ShareHolder2Parser implements Parser<ShareHolder> {
	Pattern[] ps = new Pattern[] {
		Pattern.compile("股东[\u4e00-\u9fa5]*数[|\u4e00-\u9fa5]*([0-9]*\\.?[0-9]*(?<=\\d))")
		/*,
		Pattern.compile("股东总人数([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东总数为([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东总数([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东人数为([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东人数([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东数为([0-9]*\\.?[0-9]*(?<=\\d))"),
		Pattern.compile("股东数([0-9]*\\.?[0-9]*(?<=\\d))")*/
	};
	Pattern d = Pattern.compile("\\d+-\\d+-\\d+");

	@Override
	public List<ShareHolder> parse(String stockCode, String html) {
		if (html != null && !"".equals(html)) {
			Document doc = Jsoup.parse(html);
			Elements trs = doc.select("pre");
			int size = trs.size();
			if (size <= 0)
				return null;
			List<ShareHolder> list = new ArrayList<ShareHolder>(size - 1);

			for (Element row : trs) {
				Elements cols = row.children();
				ListIterator<Element> colIter = cols.listIterator();
				while (colIter.hasNext()) {
					Element e = colIter.next();
					if (e.tagName().equals("div"))
						continue;

					List<String> matches = new ArrayList<String>();
					for(Pattern p: ps) {
						String res = doMatch(p, e.text());
						if(res != null)
							matches.add(res);
					}
					for(String match: matches) {
						String[] kvs = match.split(",");
						DateTime dateTime = new DateTime(kvs[0]); 
						long date = DateUtil.getMilliseconds(dateTime); 
						double totalHolder = NumberUtil.formatNumber(kvs[1]);
						if(totalHolder < 100)
							totalHolder = totalHolder * 10000;
						ShareHolder sh = new ShareHolder(stockCode, date, totalHolder, 0); 
						list.add(sh);
					}
				}
			}
			return list;
		}
		return null;
	}
	
	private String doMatch(Pattern p, String str) {
		Matcher matcher = p.matcher(str);
		if (matcher.find()) {
			String tsh = matcher.group(1);
			matcher = d.matcher(str);
			if (matcher.find()) {
				return matcher.group() + "," + tsh;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		Crawler sd = new SimpleHttpCrawler();
		String stockCode = "SZ000917";
		Site site = Site
				.getInstance()
				.setCharset("UTF-8")
				.setUrl("http://webf10.gw.com.cn/SZ/B6/" + stockCode
						+ "_B6.html");
		String html = sd.crawl(site).getContent();

		ShareHolder2Parser jp = new ShareHolder2Parser();
		List<ShareHolder> scs = jp.parse(stockCode, html);
		System.out.println(scs);
	}

}
