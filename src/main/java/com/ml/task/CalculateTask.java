package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.model.StatsResult;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class CalculateTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CalculateTask.class);

	/*
	 * 1. 100个交易日内的换手率，超过300%，也就是说在这100天的平均换手率超过3% (平均换手率：100天总换手率/100)
	 * 2. 对比再前100交易日的平均换手率，这100天超过了5倍以上
	 * 3. 这100天内从100天到今天股价涨幅不超过50%
	 * 4. 目前股价在在5日，10日，20日，30日均线附近，也就是说5日，10日，20日，30日的均价几乎相同，相差在5%以内
	 * 
	 */
	
	private MongoDB mongodb;
	private List<String> stockCodes;
	private List<String> dataList;
	
	public CalculateTask(MongoDB mongodb, List<String> stockCodes, List<String> dataList) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.dataList = dataList;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		logger.info("begin to run calculate: " + dataList.size());
		try{
			for (String date : dataList) {
				Map<Integer, Integer> stats = new HashMap<Integer, Integer>();

				for (String line : stockCodes) {
					String stockCode = "cn_" + line.split(",")[0];
					int res = this.calculate(stockCode, new DateTime(date));
					Integer tmp = stats.get(res);
					if(tmp == null) {
						tmp = new Integer(0);
					}
					stats.put(res, tmp + 1);
				}
				logger.info("Date: " + date + ", stats: " + stats);
				StatsResult statsResult = new StatsResult(stats.toString(), DateUtil.getMilliseconds(date));
				mongodb.save(statsResult, Constants.StatsResultCollectionName);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("cost time: " + (end - start));
	}
	
	public int calculate(String stockCode, DateTime theDate) throws Exception {
		int flag = 0;
		// 得到目前和一百天前的ScenarioResult
		DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDate, Constants.BaseDays, false);
		long theDateSecs = DateUtil.getMilliseconds(theDate);
		long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);
		//System.out.println("beforeDate: " + beforeDate);

		ScenarioResult theDateSR = getQuerySR(stockCode, theDateSecs);
		ScenarioResult beforeDateSR = getQuerySR(stockCode, beforeDateSecs);
		if(theDateSR == null || beforeDateSR == null)
			return flag;
		//System.out.println(theDateSR + "_" + beforeDateSR);
		
		//平均换手率超过3%
		flag = 1;
		if( theDateSR.getAvgTurnOverRate() < 3 ) {
			return flag;
		}
		//100天涨幅不超过50%
		flag = 2;
		if( theDateSR.getTotalChangeRate() >= 50 ) {
			return flag;
		}
		//对比前100交易日的平均换手率，这100天超过了2倍以上
		flag = 3;
		if( theDateSR.getAvgTurnOverRate() / beforeDateSR.getAvgTurnOverRate() < 2 ) {
			return flag;
		}
		//这个就用目前股价在5日，或十日附近(2%)，然后
		flag = 4;
		double nowPrice = theDateSR.getPrice();
		double fiveDiff = Math.abs(nowPrice - theDateSR.getFiveAP()) / nowPrice;
		double tenDiff = Math.abs(nowPrice - theDateSR.getTenAP()) / nowPrice;
		if( fiveDiff > 0.02 || tenDiff > 0.02 ) {
			return flag;
		}
		//4个均价相差10%以内来代替(基数为4个均价的均值)
		flag = 5;
		double avgOfAP = (theDateSR.getFiveAP() + theDateSR.getTenAP() +  theDateSR.getTwentyAP() + theDateSR.getThirtyAP()) / 4;
		double fiveDiffOfAP = Math.abs(avgOfAP - theDateSR.getFiveAP()) / avgOfAP;
		double tenDiffOfAP = Math.abs(avgOfAP - theDateSR.getTenAP()) / avgOfAP;
		double twentyDiffOfAP = Math.abs(avgOfAP - theDateSR.getTwentyAP()) / avgOfAP;
		double thirtyDiffOfAP = Math.abs(avgOfAP - theDateSR.getThirtyAP()) / avgOfAP;
		if( fiveDiffOfAP > 0.1 || tenDiffOfAP > 0.1 || twentyDiffOfAP > 0.1 || thirtyDiffOfAP > 0.1) {
			return flag;
		}
		//5日均价大于10日均价，10日均价大于20均价，20日均价大于30日均价
		flag = 6;
		if( !(theDateSR.getFiveAP() <= theDateSR.getTenAP() 
				&& theDateSR.getTenAP() <= theDateSR.getTwentyAP() 
				&& theDateSR.getTwentyAP() <= theDateSR.getThirtyAP()) ) {
			return flag;
		}
		flag = 7;
		logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
		MatchResult matchResult = new MatchResult(stockCode, DateUtil.getMilliseconds(theDate));
		mongodb.save(matchResult, Constants.MatchResultCollectionName);
		return flag;
	}
	
	private ScenarioResult getQuerySR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}

	public static void main(String[] args) throws Exception {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		String stockCode = "cn_002306";
		CalculateTask sm = new CalculateTask(mongodb, null, null);
		int flag = sm.calculate(stockCode, new DateTime("2013-10-18"));
		System.out.println(flag);
	}

	
}
