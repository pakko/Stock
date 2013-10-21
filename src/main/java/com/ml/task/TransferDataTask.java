package com.ml.task;

import hirondelle.date4j.DateTime;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class TransferDataTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(CalculateTask.class);

	private MongoDB mongodb;
	private List<String> stockCodes;
	private List<String> dataList;
	
	public TransferDataTask(MongoDB mongodb, List<String> stockCodes, List<String> dataList) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dataList = dataList;
	}
	

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try{
			logger.info("begin to run calculate: " + dataList.size());
			for (String date : dataList) {
				for (String line : stockCodes) {
					String stockCode = "cn_" + line.split(",")[0];
					this.transfer(stockCode, new DateTime(date));
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("cost time: " + (end - start));
	}
	/*
	 * steps: 
	 * 	1) 计算1年的所有100天的换手率及涨幅
	 * 	2) start date的股价和5日，10日，20日，30日均价的比值
	 */
	public void transfer(String stockCode, DateTime theDate) {
		try{
			//DateTime beforeDate = theDate.minusDays(Constants.BaseDays);
			DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDate, Constants.BaseDays, false);
			
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);
	
			//get 100 days' stock data
			Query query = new Query();
			query.addCriteria(Criteria.where("code").is(stockCode));
			query.addCriteria(Criteria.where("date").gte(beforeDateSecs).lte(theDateSecs));
			List<Stock> stockList = mongodb.find(query, Stock.class, Constants.StockCollectionName);
			
			if(stockList.size() <= 0)
				return;
			
			//the present stock price
			double stockPrice = stockList.get(0).getClose();
			
			//1, calculate turnOverRate and totalChangeRate
			double turnOverRate = 0.0;
			double totalChangeRate = 0.0;
			for(Stock stock: stockList) {
				totalChangeRate += stock.getChangeRate();
				turnOverRate += stock.getTurnOverRate();
			}
			turnOverRate = turnOverRate / stockList.size();
			
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
