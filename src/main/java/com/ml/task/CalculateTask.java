package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
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
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.model.StatsResult;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class CalculateTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CalculateTask.class);

	/*
	 * 1. 计算100天内的平均换手率
	 * 2. 计算前100天内的平均换手率
	 * 3. 100天内平均换手率大于前100天换手率
	 * 3. 计算5日，10日，20日，30日，60日均价
	 * 4. 5日和10日均价大于20日均价，20日均价大于30日均价，30日均价大于60日均价
	 * 5. 100日内涨幅小于等于50%
	 * 6. 如果近10日平均换手率大于前10日平均换手率，则5日均价必须大于10日均价，当前价格离5日均价不高于3%
	 * 7. 如果近10日平均换手率小于前10日平均换手率， 则5日均价与10日均价相差不大于2%，当前价格就在5日，10日均价附近，且连续三天以上都在这个价位附近
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

				for (String stockCode : stockCodes) {
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
		try{
			// 得到目前和一百天前的ScenarioResult
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDateSecs, 120, false);
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);
			DateTime beforeDate_10 = DateUtil.getIntervalWorkingDay(theDateSecs, 10, false);
			long beforeDateSecs_10 = DateUtil.getMilliseconds(beforeDate_10);
			//System.out.println(theDateSecs + "\n" + beforeDateSecs + "\n" + beforeDateSecs_10);

			ScenarioResult theDateSR = getQuerySR(stockCode, theDateSecs);
			ScenarioResult beforeDateSR = getQueryNearSR(stockCode, beforeDateSecs);
			ScenarioResult beforeDateSR_10 = getQueryNearSR(stockCode, beforeDateSecs_10);
			//System.out.println(theDateSR + "\n" + beforeDateSR + "\n" + beforeDateSR_10);
			if(theDateSR == null || beforeDateSR == null || beforeDateSR_10 == null) 
				return flag;
			
			//当前100天平均换手率大于前100天换手率
			flag = 1;
			if( theDateSR.getHsl120() <= beforeDateSR.getHsl120() ) {
				return flag;
			}
			
			//5日，10日均价大于20日均价，20日均价大于30日均价，30日均价大于60日均价
			//保证股票运行在上升通道中 
			flag = 2;
			if( !(theDateSR.getMa5() > theDateSR.getMa10() 
					&& theDateSR.getMa10() > theDateSR.getMa20() 
					&& theDateSR.getMa20() > theDateSR.getMa30()
					&& theDateSR.getMa30() > theDateSR.getMa60()) ) {
				return flag;
			}
			
			//涨幅不能大于50%
			flag = 3;
			if (theDateSR.getUp120() >= 50) {
				return flag;
			}
			
			//如果近10日平均换手率大于前10日平均换手率，则5日均价必须大于10日均价，当前价格离5日均价不高于3%
			flag = 4;
			double nowPrice = theDateSR.getNowPrice();
			double fiveDiff = Math.abs(nowPrice - theDateSR.getMa5()) / nowPrice;
			double five_ten_Diff = Math.abs(theDateSR.getMa5() - theDateSR.getMa10()) / theDateSR.getMa5();
			if (theDateSR.getHsl10() > beforeDateSR_10.getHsl10()) {
				if (theDateSR.getMa5() < theDateSR.getMa10()) {
					return flag;
				}
				
				flag = 5;
				if (fiveDiff > 0.03) {
					return flag;
				}
			} else {
				//如果近10日平均换手率小于前10日平均换手率， 则5日均价与10日均价相差不大于2%，当前价格就在5日，10日均价附近，且连续三天以上都在这个价位附近
				flag = 6;
				if (five_ten_Diff > 0.01) {
					return flag;
				}
				
				flag = 7;
				if (fiveDiff > 0.01) {
					return flag;
				}
			}
			flag = 8;
			DateTime beforeDate_1 = DateUtil.getIntervalWorkingDay(theDateSecs, 1, false);
			long beforeDateSecs_1 = DateUtil.getMilliseconds(beforeDate_1);
			ScenarioResult theDateSR_1 = getQuerySR(stockCode, beforeDateSecs_1);

			DateTime beforeDate_2 = DateUtil.getIntervalWorkingDay(beforeDateSecs_1, 1, false);
			long beforeDateSecs_2 = DateUtil.getMilliseconds(beforeDate_2);
			ScenarioResult theDateSR_2 = getQuerySR(stockCode, beforeDateSecs_2);

			if (!(theDateSR.getMa5() > theDateSR_1.getMa5() &&
					theDateSR_1.getMa5() > theDateSR_2.getMa5())) {
				return flag;
			}
			flag = 9;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			MatchResult matchResult = new MatchResult(stockCode, DateUtil.getMilliseconds(theDate));
			mongodb.save(matchResult, Constants.MatchResultCollectionName);
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
		}
		return flag;
	}
	
	private ScenarioResult getQuerySR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	private ScenarioResult getQueryNearSR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(date));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
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
		
		String stockCode = "cn_600803";
		CalculateTask sm = new CalculateTask(mongodb, null, null);
		int flag = sm.calculate(stockCode, new DateTime("2013-10-23"));
		System.out.println(flag);
	}

	
}
