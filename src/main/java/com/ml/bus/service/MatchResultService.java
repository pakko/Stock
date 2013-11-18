package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.MatchResultDAO;
import com.ml.model.DdzStock;
import com.ml.model.MatchResult;
import com.ml.util.DateUtil;

@Service
public class MatchResultService {

	@Autowired
	MatchResultDAO matchResultDAO;
	
	public void clearMatchResult(String startDate, String endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		matchResultDAO.delete(query);
	}
	
	public List<MatchResult> findAll() {
		return matchResultDAO.findAll();
	}

	public List<MatchResult> findByDate(String startDate, String endDate) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		List<MatchResult> items = matchResultDAO.find(query);
		return items;
	}
	
	public List<MatchResult> findByCode(String code) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		List<MatchResult> items = matchResultDAO.find(query);
		return items;
	}
	
	public List<MatchResult> findByCodeAndDate(String code, String startDate, String endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		List<MatchResult> items = matchResultDAO.find(query);
		return items;
	}

	public List<DdzStock> findDDZByDate(String stockCode, long startDate, long endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(startDate).lte(endDate));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
		return matchResultDAO.findDDZ(query);
	}
}
