package com.ml.strategy;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public abstract class AbstractStrategy implements Strategy {
	private MongoDB mongodb;
	
	public AbstractStrategy(MongoDB mongodb) {
		this.mongodb = mongodb;
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
	
	protected void saveMatchResult(String stockCode, String theDate) {
		MatchResult matchResult = new MatchResult(stockCode, DateUtil.getMilliseconds(theDate));
		mongodb.save(matchResult, Constants.MatchResultCollectionName);
	}
	
}