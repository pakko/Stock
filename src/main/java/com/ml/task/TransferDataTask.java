package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.RealStock;
import com.ml.model.ScenarioResult;
import com.ml.model.ShareCapital;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class TransferDataTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(TransferDataTask.class);

	private MongoDB mongodb;
	private List<String> stockCodes;
	private List<String> dataList;
	private boolean isReal;
	
	public TransferDataTask(MongoDB mongodb, List<String> stockCodes, 
			List<String> dataList, boolean isReal) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dataList = dataList;
		this.isReal = isReal;
	}
	

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		try{
			logger.info("begin to run transfer: " + dataList.size());
			for (String date : dataList) {
				Map<Integer, Integer> stats = new HashMap<Integer, Integer>();
				for (String stockCode : stockCodes) {
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
		TransferDataTask rdt = new TransferDataTask(mongodb, null, null, false);
		String stockCode = "sz002306";
		rdt.transfer(stockCode, new DateTime("2013-10-24"));
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
			DateTime beforeDate = DateUtil.getBeforeWorkingDay(theDateSecs, Constants.BaseDays);
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);

			double stockPrice;
			flag = 1;
			if(isReal) {
				RealStock theDateStock = getQueryRealStock(stockCode, theDateSecs);
				if(theDateStock == null) {
					return flag;
				}
				stockPrice = theDateStock.getClose();
			}
			else {
				Stock theDateStock = getQueryStock(stockCode, theDateSecs);
				if(theDateStock == null) {
					return flag;
				}
				stockPrice = theDateStock.getClose();
			}
			Stock beforeDateStock = getQueryNearStock(stockCode, beforeDateSecs);
			
			if(beforeDateStock == null) {
				return flag;
			}
			//get 250 days' stock data
			List<Stock> stockList = getQueryBetweenStocks(stockCode, beforeDateStock.getDate(), theDateSecs);
			int stockSize = stockList.size();
			flag = 2;
			//ignore the data size less than half of 250
			if( !(stockSize > 0 && stockSize > Constants.BaseDays / 2.0)) {
				//logger.warn("Stock code: " + stockCode + ", date: " + theDate + ", stockSize: " + stockSize + " is two small");
				return flag;
			}
			
			//ltp
			ShareCapital sc = getQueryShareCapital(stockCode, theDateSecs);
			flag = 3;
			if(sc == null) {
				logger.warn("Stock code: " + stockCode + ", date: " + theDate + " has no share capital");
				return flag;
			}
			double ltp = sc.getTradableShare();
			
			//1, calculate average price
			double ma5 = getDaysOfAveragePrice(stockList, 5);
			double ma10 = getDaysOfAveragePrice(stockList, 10);
			double ma20 = getDaysOfAveragePrice(stockList, 20);
			double ma30 = getDaysOfAveragePrice(stockList, 30);
			double ma60 = getDaysOfAveragePrice(stockList, 60);
			double ma120 = getDaysOfAveragePrice(stockList, 120);
			double ma250 = getDaysOfAveragePrice(stockList, stockSize);		//for 250 days' data not enough
			
			//2, calculate hsl
			double hsl5 = getDaysOfTurnOverRate(stockList, 5);
			double hsl10 = getDaysOfTurnOverRate(stockList, 10);
			double hsl20 = getDaysOfTurnOverRate(stockList, 20);
			double hsl30 = getDaysOfTurnOverRate(stockList, 30);
			double hsl60 = getDaysOfTurnOverRate(stockList, 60);
			double hsl120 = getDaysOfTurnOverRate(stockList, 120);
			double hsl250 = getDaysOfTurnOverRate(stockList, stockSize);	//for 250 days' data not enough

			//3, calculate up
			double up5 = getDaysOfUp(stockList, 5);
			double up10 = getDaysOfUp(stockList, 10);
			double up20 = getDaysOfUp(stockList, 20);
			double up30 = getDaysOfUp(stockList, 30);
			double up60 = getDaysOfUp(stockList, 60);
			double up120 = getDaysOfUp(stockList, 120);
			double up250 = getDaysOfUp(stockList, stockSize);	//for 250 days' data not enough
			
			//4, save results
			ScenarioResult smr = new ScenarioResult(stockCode, theDateSecs, stockPrice, ltp,
					ma5, ma10, ma20, ma30, ma60, ma120, ma250,
					hsl5, hsl10, hsl20, hsl30, hsl60, hsl120, hsl250,
					up5, up10, up20, up30, up60, up120, up250, isReal);
			mongodb.save(smr, Constants.ScenarioResultCollectionName);
			flag = 4;
		} catch(Exception e) {
			logger.error("Error on transfer: " + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

	private Stock getQueryStock(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
	}
	
	private RealStock getQueryRealStock(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gt(date));
		query.with(new Sort(new Sort.Order(Direction.DESC, "date")));
		return mongodb.findOne(query, RealStock.class, Constants.RealStockCollectionName);
	}
	
	private Stock getQueryNearStock(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(date));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
		return mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
	}
	
	private List<Stock> getQueryBetweenStocks(String stockCode, long beginDate, long endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(beginDate).lte(endDate));
		query.with(new Sort(new Sort.Order(Direction.DESC, "date")));
		return mongodb.find(query, Stock.class, Constants.StockCollectionName);
	}
	
	private ShareCapital getQueryShareCapital(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.with(new Sort(new Sort.Order(Direction.DESC, "date")));
		List<ShareCapital> scs = mongodb.find(query, ShareCapital.class, 
				Constants.ShareCapitalCollectionName);
		int size = scs.size();
		if(size <= 0)
			return null;
		for(int i = 0; i < size; i++) {
			long diff = date - scs.get(i).getDate();
			if(diff > 0)
				return scs.get(i);
		}
		return scs.get(size - 1);
	}
	
	private double getDaysOfAveragePrice(List<Stock> stockList, int days) {
		double ap = 0.0;
		if (stockList.size() >= days) {
			for (int i = 0; i < days; i++) {
				ap += stockList.get(i).getClose();
			}
			ap = ap / days;
		}
		return ap;
	}
	
	private double getDaysOfTurnOverRate(List<Stock> stockList, int days) {
		double avgTurnOverRate = 0.0;
		if (stockList.size() >= days) {
			for (int i = 0; i < days; i++) {
				avgTurnOverRate += stockList.get(i).getTurnOverRate();
			}
			avgTurnOverRate = avgTurnOverRate / days;
		}
		return avgTurnOverRate;
	}
	

	
	private double getDaysOfUp(List<Stock> stockList, int days) {
		double up = 0.0;
		if (stockList.size() >= days) {
			for (int i = 0; i < days; i++) {
				up += stockList.get(i).getChangeRate();
			}
			up = up / days;
		}
		return up;
	}

	/*
	 * var mapFunction = function() {
	var key = this.code;
	var value = {
		date: this.date,
		nowPrice: this.close,
		close: this.close,
		ma5: this.ma5
	};
	emit( key, value );
};

var reduceFunction = function(key, values) {
	var reducedObject = {
		code: key,
		ma250: 0
	};
	print(values.length);
	values.forEach( function(value) {
		reducedObject.ma250 += value.close;
	});
	return reducedObject;
};

var finalizeFunction = function (key, reducedValue) {
	if (reducedValue.count > 0)
		reducedValue.ma250 = reducedValue.ma250 / reducedValue.count;

	return reducedValue;
};

var v_ltp = {ltp: 2013};
db.stock.mapReduce( 
	function() {
		var key = this.code;
		var value = {
			date: this.date,
			nowPrice: this.close,
			ma5: this.ma5,
			count: 1
		};
		emit( key, value );
	},
	function(key, values) {
		var reducedObject = {
			code: key,
			ma250: 0,
			nowPrice: 0,
			count: 0,
			date: 0
		};
		print(values.length);
		values.forEach( function(value) {
			if(reducedObject.date < value.date)
				reducedObject.date = value.date
			reducedObject.ma250 += value.nowPrice;
			reducedObject.count += value.count
		});
		return reducedObject;
	},
	{
	 query: { code: "sz002306", date: { $gte: 1349712000000, $lte: 1382544000000 } },
	 sort:  { code: -1, date: -1},
	 out:   { merge: "test" },
	 finalize: function (key, reducedValue) {
		if(reducedValue.count > 0)
			reducedValue.ma250 = reducedValue.ma250 * 1.0 / reducedValue.count;

		return reducedValue;
	}
});
 scope: { v_ltp: v_ltp },
 db.stock.find({date: { $gte: 1349712000000, $lte: 1382630400000}}).sort({ code: -1, date: -1});

 	reducedObject.date = value.date;
	reducedObject.nowPrice = value.nowPrice;
	reducedObject.ma5 = value.ma5;
 var reducedObject = {
		code: key,
		date: 0,
		nowPrice: 0,
		ltp: v_ltp.ltp,
		ma5: 0,
		ma250: 0
	};
	 */
}
