package com.ml.task;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.Stock;
import com.ml.qevent.QueueListenerManager;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class RetrieveDataTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(RetrieveDataTask.class);

	private MongoDB mongodb;
	private List<String> stockCodes;
	private String[] dates;
	private QueueListenerManager manager;
	
	private String periodMethod = "d";
	private String resultMethod = "js";
	
	public RetrieveDataTask(MongoDB mongodb, List<String> stockCodes,
			String[] dates, QueueListenerManager manager) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dates = dates;
		this.manager = manager;
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		logger.info("start date: " + dates[0] + ", end date: " + dates[1]);
		for (String line : stockCodes) {
			//String stockCode = "cn_002306";
			String stockCode = "cn_" + line.split(",")[0];
			this.process(stockCode);
		}
		long end = System.currentTimeMillis();
		logger.info("cost time: " + (end - start));
		manager.fireWorkspaceCommand("take_retrieved_data");
	}
	
	public void process(String stockCode) {
		String retrieveUrl = "http://q.stock.sohu.com/app2/history.up?method=history"
				+ "&code=" + stockCode
				+ "&sd=" + dates[0]
				+ "&ed=" + dates[1]
				+ "&t=" + periodMethod 
				+ "&res=" + resultMethod;
		try {
			// get data and remove some tags
			String result = this.downLoadPages(retrieveUrl);
			result = this.formatUrlResult(result);

			// use jackson to translate string to json list
			ObjectMapper objectMapper = new ObjectMapper();
			List<List<String>> resultList = objectMapper.readValue(result, List.class);
			
			// translate lists to object
			Set<Stock> stockList = this.listToStockBean(stockCode, resultList);
			
			// save to db
			mongodb.insert(stockList, Constants.StockCollectionName);
			//logger.info("store stock size: " + stockList.size());

		} catch (IOException e) {
			System.err.println("Download, Stock code: " + stockCode + 
					", start date [" + dates[0] +"], end date [" + dates[1] + "] ---" + e.getMessage());
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
		}
		return stockList;
	}

}
