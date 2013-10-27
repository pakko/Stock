package com.ml.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.crawler.Crawler;
import com.ml.crawler.ResultItems;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.db.MongoDB;
import com.ml.model.Stock;
import com.ml.parser.Parser;
import com.ml.parser.StockParser;
import com.ml.pipeline.MongoDBPipeline;
import com.ml.pipeline.Pipeline;
import com.ml.util.Constants;

import java.util.List;
import java.util.Properties;

/*
 * retrieve stock data
 */
public class RetrieveStockDataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveStockDataTask.class);

	private static final String KDaily = "http://api.finance.ifeng.com/index.php/akdaily/?code=";
	private static final String KDailyType = "&type=last";
	private static final String KDailyFQType = "&type=fq";
	private static final String DefaultCharset = "GBK";
	
	private List<String> stockCodes;
	private Crawler crawler;
	private Parser<Stock> parser;
	private Pipeline<Stock> pipeline;
	private String type;	//last or fq
	
	public RetrieveStockDataTask(MongoDB mongodb, List<String> stockCodes, 
			String type, boolean isLatest) {
		this.stockCodes = stockCodes;
		
		crawler = new SimpleHttpCrawler();
		parser = new StockParser(isLatest);
		//pipeline = new MemoryPipeline<Stock>();
		pipeline = new MongoDBPipeline<Stock>(mongodb, Constants.StockCollectionName);
		this.type = type;
	}

	@Override
	public void run() {
		try{
			long start = System.currentTimeMillis();
			logger.info("start retrieve stock data, size: " + stockCodes.size());
			for (String stockCode : stockCodes) {
				this.process(stockCode);
			}
			long end = System.currentTimeMillis();
			logger.info("cost time: " + (end - start));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process(String stockCode) {
		String kType = type.equals("fq") ? KDailyFQType : KDailyType;
		String retrieveUrl = KDaily + stockCode + kType;
		Site site = Site.getInstance().setCharset(DefaultCharset)
				.setUrl(retrieveUrl);
		
		//get content
		ResultItems res = crawler.crawl(site);
		if(res == null)
			return;
		//parse into stocks
		List<Stock> scs = parser.parse(stockCode, res.getContent());
		//save to db
		pipeline.process(scs);
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		RetrieveStockDataTask rdt = new RetrieveStockDataTask(mongodb, null, "last", true);
		String stockCode = "sz002306";
		rdt.process(stockCode);
	}

}

