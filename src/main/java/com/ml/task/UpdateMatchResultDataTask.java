package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class UpdateMatchResultDataTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(UpdateMatchResultDataTask.class);

	private MongoDB mongodb;
	
	public UpdateMatchResultDataTask(MongoDB mongodb) {
		this.mongodb = mongodb;
	}
	

	@Override
	public void run() {
		logger.info("begin to update match result");
		update();
		logger.info("end of update match result");
	}
	
	private List<MatchResult> getMatchResult() {
		Query query = new Query();
		//query.addCriteria(Criteria.where("code").is("sh600432"));
		query.addCriteria(Criteria.where("strategy").is("StrategyF"));
		return mongodb.find(query, MatchResult.class, Constants.MatchResultCollectionName);
		//return mongodb.findAll(MatchResult.class, Constants.MatchResultCollectionName);
	}
	
	private ScenarioResult getSC(String code, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	private final double ECONS = 0.00001;
	private void update() {
		DateTime d = DateUtil.getIntervalWorkingDay(new Date().getTime(), 1, false);
    	long date = DateUtil.getMilliseconds(d);
		
    	List<MatchResult> mrs = getMatchResult();
    	
		for(MatchResult mr: mrs){
			mongodb.delete(mr, Constants.MatchResultCollectionName);
			
			double d5 = 0;
			double d10 = 0;
			double dnow = 0;
			
			String stockCode = mr.getCode();
			long mrDate;
			if(mr.getStrategy().equals("StrategyF")) {
				mrDate = mr.getFlyDate();
			}
			else {
				mrDate = mr.getDate();
			}
			DateTime afterDate5 = DateUtil.getIntervalWorkingDay(mrDate, 5, true);
			DateTime afterDate10 = DateUtil.getIntervalWorkingDay(mrDate, 10, true);
			
			ScenarioResult theSR = getSC(stockCode, mrDate);
			ScenarioResult stockAfter5 = getSC(stockCode, DateUtil.getMilliseconds(afterDate5));
			ScenarioResult stockAfter10 = getSC(stockCode, DateUtil.getMilliseconds(afterDate10));
			ScenarioResult stockNow = getSC(stockCode, date);
			
			double price = theSR.getNowPrice();
			
			if(stockAfter5 != null) {
				d5 = (stockAfter5.getNowPrice() - price) / (price + ECONS);
			}
			if(stockAfter10 != null) {
				d10 = (stockAfter10.getNowPrice() - price) / (price + ECONS);
			}
			if(stockNow != null) {
				dnow = (stockNow.getNowPrice() - price) / (price + ECONS);
			}
			mr.setD5(d5);
			mr.setD10(d10);
			mr.setDnow(dnow);
			mongodb.save(mr, Constants.MatchResultCollectionName);
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
		UpdateMatchResultDataTask rdt = new UpdateMatchResultDataTask(mongodb);
		rdt.update();
	}
}
