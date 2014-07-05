package com.ml.regression;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.crawler.HttpClientCrawler;
import com.ml.crawler.ResultItems;
import com.ml.crawler.Site;
import com.ml.db.MongoDB;
import com.ml.util.Constants;

@Service
public class Regression {
	
	private final String COMMUNITY_URL = "http://esf.sh.soufun.com/housingshequ/25_1644_0_0_0_0_1_0_0/";
	
	private MongoDB mongo;
	
	public void parseCommunityInfo(String html)  {
		String name = null;
		try{
	        Document doc = Jsoup.parse(html);
	        Elements trs = doc.select("#houselist li");
	        for (Element row: trs) {  
	        	Elements cols = row.children();
	        	Elements dt = cols.select("dl>dt");
	        	
	        	String type = dt.select(">img").attr("src");
	        	if(type.contains("shangpu") || type.contains("xiezilou")
	        			|| type.contains("dotzs-new"))
	        		continue;
	        	
	        	Elements title = dt.select(">a");
	        	String communityUrl = title.attr("href");
	        	name = title.text();
	        	
	        	String majorLocation = dt.select("span>a").text();
	        	String minorLocation = dt.after("span").text();
	        	
	        	Elements dd = cols.select("dl>dd");
	        	Element num = dd.select(">a").get(0);
	        	Integer number = getNumbers(num.text());
	        	String housesUrl = "http://esf.sh.soufun.com" + num.attr("href");
	        	
	        	Elements div = cols.select("div.price");
	        	String price = div.select("a>span").text();
	        	String up = div.select("span.number").text();
	        	Double avgPrice = Double.valueOf(price);
	        	Double upRate = Double.valueOf(up.substring(1, up.length() -1));
	        	
	        	Community c = new Community(communityUrl, housesUrl, name, null, number, majorLocation, minorLocation, avgPrice, upRate, null);
	        	mongo.save(c, "community");
	        	
	        	System.out.println(c);
	        }
		} catch(Exception e){
			System.err.println("error url:" + name);
			e.printStackTrace();
		}
	}
	
	SortedMap<Integer, Integer> years = new TreeMap<Integer, Integer>();
	
	public List<House> parseHouseInfo(String html)  {
		String url = null;
		List<House> houses = null;
		try{
	        Document doc = Jsoup.parse(html);
	        Elements trs = doc.select("div.houseList>dl");
	        houses = new ArrayList<House>(trs.size());
	        for (Element row: trs) {  
	        	Elements cols = row.children();
	        	
	        	Elements title = cols.select("dd>p.title>a");
	        	String name = title.text();
	        	url = "http://esf.sh.soufun.com" + title.attr("href");
	        	
	        	Elements ps = cols.select("dd>p");
	        	String[] info = ps.get(2).text().split("/");
	        	Integer layout = Integer.valueOf(info[0].substring(0,1));
	        	Integer floor = Integer.valueOf(info[1]);
	        	Integer totalFloor = Integer.valueOf(getNumbers(info[2]));
	        	if(info.length == 5) {
		        	Integer year = getNumbers(info[4]);
		        	Integer mapYear = years.get(year);
		        	if(mapYear == null) {
		        		mapYear = new Integer(0);
		        	}
		        	years.put(year, mapYear+1);
	        	}
	        	
	        	String area = cols.select("div.area").text();
	        	Double squarFeet = getNumbers(area) * 1.0;
	        	
	        	String price = cols.select("div.moreInfo span.price").text();
	        	Double totalPrice = getNumbers(price) * 1.0;
	        	String avg = cols.select("div.moreInfo>p.danjia").text();
	        	Double avgPrice = getNumbers(avg) * 1.0;
	        	
	        	House house = new House(name, url, squarFeet, totalPrice, layout, floor, totalFloor, avgPrice);
	        	//System.out.println(house);
	        	houses.add(house);
	        	//System.exit(0);
	        }
		} catch(Exception e){
			System.err.println("error url:" + url);
			e.printStackTrace();
		}
		return houses;
	}
	
	public Integer getNumbers(String content) {  
       Pattern pattern = Pattern.compile("\\d+");  
       Matcher matcher = pattern.matcher(content);  
       while (matcher.find()) {  
           return Integer.parseInt(matcher.group(0));  
       }  
       return -1;
	}
	
	public void crawlCommunity(String id) {
		HttpClientCrawler hc = new HttpClientCrawler();
		Site site = Site.getInstance().setCharset("gb2312").setUrl(COMMUNITY_URL);
		ResultItems res = hc.crawl(site);
		int pageNum = getCommunityPage(res.getContent());
		
		for(int i = 1; i <= pageNum; i++) {
			String url = "http://esf.sh.soufun.com/housingshequ/" + id + "_0_0_0_0_" + i + "_0_0/";
			System.out.println(url);
			site.setUrl(url);
			ResultItems r = hc.crawl(site);
			parseCommunityInfo(r.getContent());
		}
		hc.close();
	}
	
	public void crawlHouses(String id) {
		Query query = new Query();
		query.addCriteria(Criteria.where("houses").exists(false));
		List<Community> communities = mongo.find(query, Community.class, "community");
		
		Site site = Site.getInstance().setCharset("gb2312");
		int n = 1;
		//Community c = communities.get(0);
		for(Community c: communities) {
			try{
				years.clear();
				
				HttpClientCrawler hc = new HttpClientCrawler();
				site.setUrl(c.getHouseUrl() + "j340/");
				ResultItems res = hc.crawl(site);
				
				int pageNum = getHousePage(res.getContent());
				
				List<House> houseList = new ArrayList<House>();
				for(int i = 1; i <= pageNum; i++) {
					String url = c.getHouseUrl() + "i3" + i + "-j340/";
					System.out.println(url);
					site.setUrl(url);
					
					ResultItems r = hc.crawl(site);
					
					List<House> houses = parseHouseInfo(r.getContent());
					houseList.addAll(houses);
				}
				c.setHouses(houseList);
				if(years.size() > 0)
					c.setDate(years.firstKey());
				//System.out.println(c);
				mongo.delete(c, "community");
				mongo.save(c, "community");
				
				System.out.println("------"+ (n++));
				years.clear();
				hc.close();
			} catch(Exception e) {
				System.err.println(c);
				e.printStackTrace();
			}
		}
		
		
	}

	private int getCommunityPage(String content) {
		Document doc = Jsoup.parse(content);
        Elements trs = doc.select("#wrap ul.mt15 li.pages");
        String page = trs.get(0).text();
        return Integer.parseInt(page.substring(page.indexOf("/")+1, page.indexOf(" 页")));
	}
	
	private int getHousePage(String content) {
		Document doc = Jsoup.parse(content);
		String page = doc.select("div.fanye>span").text();
        return getNumbers(page);
	}
	
	private void init() {
		String confFile = Constants.DefaultConfigFile;
		Properties props = new Properties();
		try {
			props.load(new FileInputStream(confFile));
		} catch (IOException e) {
			System.out.println(e.toString());
			return;
		}
		mongo = new MongoDB(props);
	}

	public static void main(String[] args) throws Exception {
		Regression r = new Regression();
		r.init();
		//r.crawlCommunity("25_1644");
		r.crawlHouses(null);
		
    }
}
