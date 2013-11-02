package com.ml.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.crawler.Crawler;
import com.ml.crawler.ResultItems;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.db.MongoDB;
import com.ml.model.DdzStock;
import com.ml.parser.DdzStockParser;
import com.ml.parser.Parser;
import com.ml.pipeline.MongoDBPipeline;
import com.ml.pipeline.Pipeline;
import com.ml.util.Constants;

import java.util.List;

/*
 * retrieve ddz stock data
 */
public class RetrieveDdzStockDataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveDdzStockDataTask.class);

	private static final String DDZ_Prefix = "http://bolelife.hk.pkidc.cn/gudata/ddz-line.asp?code=";
	private static final String DefaultCharset = "GBK";
	
	private List<String> stockCodes;
	private Crawler crawler;
	private Parser<DdzStock> parser;
	private Pipeline<DdzStock> pipeline;
	
	public RetrieveDdzStockDataTask(MongoDB mongodb, List<String> stockCodes) {
		this.stockCodes = stockCodes;
		
		crawler = new SimpleHttpCrawler();
		parser = new DdzStockParser();
		pipeline = new MongoDBPipeline<DdzStock>(mongodb, Constants.DDZStockCollectionName);
	}

	@Override
	public void run() {
		try{
			long start = System.currentTimeMillis();
			logger.info("start retrieve ddz stock data, size: " + stockCodes.size());
			
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
		String retrieveUrl = DDZ_Prefix + stockCode.substring(2);
		Site site = Site.getInstance().setCharset(DefaultCharset)
				.setUrl(retrieveUrl);
		
		//get content
		ResultItems res = crawler.crawl(site);
		if(res == null)
			return;
		//parse into stocks
		List<DdzStock> scs = parser.parse(stockCode, res.getContent());
		//save to db
		pipeline.process(scs);
	}

}

