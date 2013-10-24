package com.ml.task;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

public class RetrieveDataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveDataTask.class);

	private MongoDB mongodb;
	private List<String> stockCodes;
	private String[] dates;
	
	private String periodMethod = "d";
	private String resultMethod = "js";
	
	public RetrieveDataTask(MongoDB mongodb, List<String> stockCodes, String[] dates) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dates = dates;
	}

	@Override
	public void run() {
		try{
			long start = System.currentTimeMillis();
			logger.info("start date: " + dates[0] + ", end date: " + dates[1]);
			for (String line : stockCodes) {
				//String stockCode = "cn_002306";
				String stockCode = "cn_" + line.split(",")[0];
				//String stockCode = "cn_" + line.substring(2);
				this.process(stockCode, dates[0], dates[1]);
			}
			long end = System.currentTimeMillis();
			logger.info("cost time: " + (end - start));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		RetrieveDataTask rdt = new RetrieveDataTask(mongodb, null, null);
		String stockCode = "cn_000025";
		rdt.process(stockCode, "2013-01-01", "2013-04-10");
	}
	
	public void process(String stockCode, String beginDate, String endDate) {
		String retrieveUrl = "http://q.stock.sohu.com/app2/history.up?method=history"
				+ "&code=" + stockCode
				+ "&sd=" + beginDate
				+ "&ed=" + endDate
				+ "&t=" + periodMethod 
				+ "&res=" + resultMethod;
		String result = null;
		try {
			// get data and remove some tags
			result = this.downLoadPages(retrieveUrl);
			result = this.formatUrlResult(result);
			if(result.equals(""))
				return;
			
			// use jackson to translate string to json list
			ObjectMapper objectMapper = new ObjectMapper();
			List<List<String>> resultList = objectMapper.readValue(result, List.class);
			
			// translate lists to object
			Set<Stock> stockList = this.listToStockBean(stockCode, resultList);
			
			// save to db
			mongodb.insert(stockList, Constants.StockCollectionName);
			//logger.info("store stock size: " + stockList.size());

		} catch (IOException e) {
			logger.error("Download, Stock code: " + stockCode + 
					", start date [" + beginDate +"], end date [" + endDate + "] , result: " + result + "---" + e.getMessage());
		}
	}
	
	private String downLoadPages(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("GET");
		InputStream in = httpConn.getInputStream();
		String content = IOUtils.toString(in, "GBK");
		// System.out.println(content);
		in.close();
		httpConn.disconnect();

		return content;
	}
	
	private String formatUrlResult(String result) {
		result = result.trim();
		result = result.replace("\n", "");
		result = result.substring("PEAK_ODIA(['hq_history',".length(),
				result.length() - 1);
		result = result.replace("'", "\"");

		//System.out.println(result);
		return result;
	}

	private Set<Stock> listToStockBean(String stockCode, List<List<String>> lists) {
		Set<Stock> stockList = new TreeSet<Stock>();
		for (List<String> list : lists) {
			try{
				long date = DateUtil.getMilliseconds(list.get(0).toString());
				double opening = Double.parseDouble(list.get(1).toString());
				double close = Double.parseDouble(list.get(2).toString());
				double change = Double.parseDouble(list.get(3).toString());
				double changeRate = Double.parseDouble(list.get(4).toString()
						.substring(0, list.get(4).toString().length() - 1));
				double min = Double.parseDouble(list.get(5).toString());
				double max = Double.parseDouble(list.get(6).toString());
				long tradeVolume = Long.parseLong(list.get(7).toString());
				double tradeAmount = Double.parseDouble(list.get(8).toString());
				double turnOverRate = 0;
				if (!list.get(9).toString().contains("-")) {
					turnOverRate = Double.parseDouble(list.get(9).toString()
							.substring(0, list.get(9).toString().length() - 1));
				}
				
				Stock stock = new Stock(stockCode, date, opening, close, 
						change, changeRate, min, max, tradeVolume, tradeAmount, turnOverRate);
				stockList.add(stock);
				//System.out.println(stock.toString());
			} catch (Exception e) {
				logger.error("StockCode: " + stockCode + ", data: " + list + ", in lists: " + lists + ", error: " + e.getMessage());
			}
		}
		return stockList;
	}

}
