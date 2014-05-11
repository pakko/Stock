package com.ml.strategy;

import java.util.List;

import hirondelle.date4j.DateTime;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public abstract class AbstractStrategy implements Strategy {
	private MongoDB mongodb;
	protected boolean isReal;
	
	public AbstractStrategy(MongoDB mongodb, Boolean isReal) {
		this.mongodb = mongodb;
		this.isReal = isReal;
	}
	
	protected List<ScenarioResult> getRangeQuerySRs(String stockCode, long startDate, long endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").lte(endDate).gte(startDate));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
		return mongodb.find(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	
	protected ScenarioResult getQuerySR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	protected ScenarioResult getQueryNearSR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(date));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	protected void saveMatchResult(String stockCode, String theDate, String strategy) {
		long date = DateUtil.getMilliseconds(theDate);
		DateTime beforeDate5 = DateUtil.getIntervalWorkingDay(date, 5, true);
		DateTime beforeDate10 = DateUtil.getIntervalWorkingDay(date, 10, true);
		
		double d = 0;
		double d5 = 0; 
		double d10 = 0; 
		ScenarioResult stock = getQuerySR(stockCode, date);
		if(stock != null) {
			d = stock.getNowPrice();
		}
		ScenarioResult stock5 = getQuerySR(stockCode, DateUtil.getMilliseconds(beforeDate5));
		if(stock5 != null && d != 0) {
			d5 = (stock5.getNowPrice() - d) / d;
		}
		ScenarioResult stock10 = getQuerySR(stockCode, DateUtil.getMilliseconds(beforeDate10));
		if(stock10 != null && d != 0) {
			d10 = (stock10.getNowPrice() - d) / d;
		}
		
		MatchResult matchResult = new MatchResult(stockCode, date, strategy, d5, d10, d);
		mongodb.save(matchResult, Constants.MatchResultCollectionName);
	}
	
	protected Stock getQueryStock(String stockCode, long date) {
        Query query = new Query();
        query.addCriteria(Criteria.where("code").is(stockCode));
        query.addCriteria(Criteria.where("date").is(date));
        return mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
    }
}