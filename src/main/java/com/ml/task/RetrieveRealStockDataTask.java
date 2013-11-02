package com.ml.task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.crawler.Crawler;
import com.ml.crawler.ResultItems;
import com.ml.crawler.SimpleHttpCrawler;
import com.ml.crawler.Site;
import com.ml.db.MongoDB;
import com.ml.model.RealStock;
import com.ml.parser.Parser;
import com.ml.parser.RealStockParser;
import com.ml.pipeline.MongoDBPipeline;
import com.ml.pipeline.Pipeline;
import com.ml.util.Constants;

import java.util.List;

/*
 * retrieve real stock data
 */
public class RetrieveRealStockDataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveRealStockDataTask.class);

	private static final String REAL_Prefix = "http://hq.finance.ifeng.com/q.php?l=";
	private static final String REAL_Post = ",&f=json";
	private static final String DefaultCharset = "GBK";
	
	private List<String> stockCodes;
	private Crawler crawler;
	private Parser<RealStock> parser;
	private Pipeline<RealStock> pipeline;
	
	public RetrieveRealStockDataTask(MongoDB mongodb, List<String> stockCodes) {
		this.stockCodes = stockCodes;
		
		crawler = new SimpleHttpCrawler();
		parser = new RealStockParser();
		pipeline = new MongoDBPipeline<RealStock>(mongodb, Constants.RealStockCollectionName);
	}

	@Override
	public void run() {
		try{
			long start = System.currentTimeMillis();
			logger.info("start retrieve real stock data, size: " + stockCodes.size());
			
			String retrieveUrl = REAL_Prefix;
			for (String stockCode : stockCodes) {
				retrieveUrl += stockCode + ",";
			}
			retrieveUrl += REAL_Post;
			this.process(retrieveUrl);
			
			long end = System.currentTimeMillis();
			logger.info("cost time: " + (end - start));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	private void process(String retrieveUrl) {
		Site site = Site.getInstance().setCharset(DefaultCharset)
				.setUrl(retrieveUrl);
		
		//get content
		ResultItems res = crawler.crawl(site);
		if(res == null)
			return;
		//parse into stocks
		List<RealStock> scs = parser.parse(null, res.getContent());
		//save to db
		pipeline.process(scs);
	}

}

