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
import com.ml.model.ShareHolder;
import com.ml.parser.Parser;
import com.ml.parser.ShareHolder2Parser;
import com.ml.pipeline.MongoDBPipeline;
import com.ml.pipeline.Pipeline;
import com.ml.util.Constants;

import java.util.List;
import java.util.Properties;

/*
 * retrieve share holder data
 */
public class RetrieveSH2DataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveSH2DataTask.class);

	private static final String GBJG_Prefix = "http://webf10.gw.com.cn/SZ/B6/";
	private static final String GBJG_Post = "_B6.html";
	private static final String DefaultCharset = "UTF-8";
	
	private List<String> stockCodes;
	private Crawler crawler;
	private Parser<ShareHolder> parser;
	private Pipeline<ShareHolder> pipeline;
	
	public RetrieveSH2DataTask(MongoDB mongodb, List<String> stockCodes) {
		this.stockCodes = stockCodes;
		
		crawler = new SimpleHttpCrawler();
		parser = new ShareHolder2Parser();
		pipeline = new MongoDBPipeline<ShareHolder>(mongodb, Constants.ShareHolderCollectionName);
	}

	@Override
	public void run() {
		try{
			long start = System.currentTimeMillis();
			logger.info("start retrieve share holder data, size: " + stockCodes.size());
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
		String retrieveUrl = GBJG_Prefix + stockCode.toUpperCase() + GBJG_Post;
		Site site = Site.getInstance().setCharset(DefaultCharset)
				.setUrl(retrieveUrl);
		
		//get content
		ResultItems res = crawler.crawl(site);
		if(res == null)
			return;
		//parse into stocks
		List<ShareHolder> scs = parser.parse(stockCode, res.getContent());
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
		
		RetrieveSH2DataTask rdt = new RetrieveSH2DataTask(mongodb, null);
		String stockCode = "sz002306";
		rdt.process(stockCode);
	}

}

