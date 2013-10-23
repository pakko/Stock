package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Order;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class TransferDataTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(TransferDataTask.class);

	private MongoDB mongodb;
	private List<String> stockCodes;
	private List<String> dataList;
	
	public TransferDataTask(MongoDB mongodb, List<String> stockCodes, 
			List<String> dataList) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dataList = dataList;
	}
	

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try{
			logger.info("begin to run transfer: " + dataList.size());
			for (String date : dataList) {
				Map<Integer, Integer> stats = new HashMap<Integer, Integer>();
				for (String line : stockCodes) {
					String stockCode = "cn_" + line.split(",")[0];
					int res = this.transfer(stockCode, new DateTime(date));
					Integer tmp = stats.get(res);
					if(tmp == null) {
						tmp = new Integer(0);
					}
					stats.put(res, tmp + 1);
				}
				logger.info("Date: " + date + ", stats: " + stats);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("cost time: " + (end - start));
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		TransferDataTask rdt = new TransferDataTask(mongodb, null, null);
		String stockCode = "cn_000553";
		rdt.transfer(stockCode, new DateTime("2013-10-17"));
	}
	/*
	 * steps: 
	 * 	1) 计算1年的所有100天的换手率及涨幅
	 * 	2) start date的股价和5日，10日，20日，30日均价的比值
	 */
	public int transfer(String stockCode, DateTime theDate) {
		int flag = 0;
		try{
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDateSecs, Constants.BaseDays, false);
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);

			Stock theDateStock = getQueryStock(stockCode, theDateSecs);
			Stock beforeDateStock = getQueryNearStock(stockCode, beforeDateSecs);
			
			flag = 1;
			if(theDateStock == null || beforeDateStock == null)
				return flag;
			
			//get 100 days' stock data
			Query query = new Query();
			query.addCriteria(Criteria.where("code").is(stockCode));
			query.addCriteria(Criteria.where("date").gte(beforeDateStock.getDate()).lte(theDateSecs));
			List<Stock> stockList = mongodb.find(query, Stock.class, Constants.StockCollectionName);
			int stockSize = stockList.size();

			flag = 2;
			if( !(stockSize > 0 && stockSize > Constants.BaseDays / 2.0))
				return flag;
			flag = 3;
			//the present stock price
			double stockPrice = stockList.get(0).getClose();
			
			//1, calculate turnOverRate and totalChangeRate
			double turnOverRate = 0.0;
			double totalChangeRate = 0.0;
			for(Stock stock: stockList) {
				totalChangeRate += stock.getChangeRate();
				turnOverRate += stock.getTurnOverRate();
			}
			turnOverRate = turnOverRate / stockSize;
			
			//2, calculate average price
			double fiveAP = getDaysOfAveragePrice(stockList, 5);
			double tenAP = getDaysOfAveragePrice(stockList, 10);
			double twentyAP = getDaysOfAveragePrice(stockList, 20);
			double thirdtyAP = getDaysOfAveragePrice(stockList, 30);
			
			//3, save results
			ScenarioResult smr = new ScenarioResult(stockCode, theDateSecs, stockPrice,
					turnOverRate, totalChangeRate, fiveAP, tenAP, twentyAP, thirdtyAP);
			mongodb.save(smr, Constants.ScenarioResultCollectionName);
		} catch(Exception e) {
			logger.error("Error on transfer: " + e.getMessage());
		}
		return flag;
	}
	
	private Stock getQueryStock(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
	}
	
	private Stock getQueryNearStock(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(date));
		query.sort().on("date", Order.ASCENDING);
		return mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
	}
	
	private List<Stock> getDaysOfData(List<Stock> stockList, int days) {
		List<Stock> stocks = new ArrayList<Stock>(days);
		int i = 0;
		Iterator<Stock> it = stockList.iterator();
		while(it.hasNext() && i < days) {
			stocks.add(it.next());
			i++;
		}
		return stocks;
	}
	
	private double getDaysOfAveragePrice(List<Stock> stockList, int days) {
		List<Stock> stocks = this.getDaysOfData(stockList, days);
		double ap = 0.0;
		for(Stock stock: stocks) {
			ap += stock.getClose();
		}
		return ap / days;
	}

}
