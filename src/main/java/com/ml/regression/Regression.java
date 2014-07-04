package com.ml.regression;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import com.ml.crawler.HttpClientCrawler;
import com.ml.crawler.ResultItems;
import com.ml.crawler.Site;
import com.ml.db.IBaseDB;
import com.ml.db.MongoDB;
import com.ml.util.Constants;

@Service
public class Regression {
	
	private final String COMMUNITY_URL = "http://esf.sh.soufun.com/housingshequ/25_1644_0_0_0_0_1_0_0/";

	private final String FORMAT_PATTERN = "yyyy-MM-dd";
	private SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_PATTERN);
	
	public void parseInfo(String html, IBaseDB m)  {
		String name = null;
		try{
	        Document doc = Jsoup.parse(html);
	        Elements trs = doc.select("#houselist li");
	        for (Element row: trs) {  
	        	Elements cols = row.children();
	        	Elements dt = cols.select("dl>dt");
	        	
	        	Elements title = dt.select(">a");
	        	String communityUrl = title.attr("href");
	        	name = title.text();
	        	
	        	String majorLocation = dt.select("span>a").text();
	        	String minorLocation = dt.after("span").text();
	        	
	        	Elements dd = cols.select("dl>dd");
	        	Element num = dd.select(">a").get(0);
	        	Integer number = getNumbers(num.text());
	        	String housesUrl = num.attr("href");
	        	
	        	Elements div = cols.select("div.price");
	        	String price = div.select("a>span").text();
	        	String up = div.select("span.number").text();
	        	Double avgPrice = Double.valueOf(price);
	        	Double upRate = Double.valueOf(up.substring(1, up.length() -1));
	        	
	        	Community c = new Community(communityUrl, housesUrl, name, null, number, majorLocation, minorLocation, avgPrice, upRate, null);
	        	
	        	System.out.println(c);
	        }
		} catch(Exception e){
			System.err.println("error url:" + name);
			e.printStackTrace();
		}
	}
	
	public Integer getNumbers(String content) {  
	       Pattern pattern = Pattern.compile("\\d+");  
	       Matcher matcher = pattern.matcher(content);  
	       while (matcher.find()) {  
	           return Integer.parseInt(matcher.group(0));  
	       }  
	       return -1;
	   } 
	
	public void crawl(IBaseDB m, String userID) {
		HttpClientCrawler hc = new HttpClientCrawler();
		Site site = Site.getInstance().setCharset("gb2312").setUrl(COMMUNITY_URL);
		ResultItems res = hc.crawl(site);
		int pageNum = getPage(res.getContent());
		
		for(int i = 1; i <= pageNum; i++) {
			String url = "http://esf.sh.soufun.com/housingshequ/25_1644_0_0_0_0_" + i + "_0_0/";
			Site s = Site.getInstance().setCharset("gb2312").setUrl(url);
			ResultItems r = hc.crawl(s);
			parseInfo(r.getContent(), m);
		}
	}
	
	private int getPage(String content) {
		Document doc = Jsoup.parse(content);
        Elements trs = doc.select("#wrap ul.mt15 li.pages");
        String page = trs.get(0).text();
        return Integer.parseInt(page.substring(0, page.indexOf("/")));
	}

	public static void main(String[] args) throws ParseException, InterruptedException, JsonParseException, JsonMappingException, IOException {
		String confFile = Constants.DefaultConfigFile;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(confFile));
		} catch (IOException e) {
			System.out.println(e.toString());
			return;
		}
		MongoDB m = new MongoDB(props);
		Regression r = new Regression();
		r.crawl(m, null);
		
		
    }
}
