package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.AnalyzerResultDAO;
import com.ml.model.AnalyzerResult;
import com.ml.util.DateUtil;

@Service
public class AnalyzerResultService {

	@Autowired
	AnalyzerResultDAO analyzerResultDAO;
	
	public void clearAnalyzerResult(String startDate, String endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		analyzerResultDAO.delete(query);
	}
	
	public List<AnalyzerResult> findAll() {
		return analyzerResultDAO.findAll();
	}

	public List<AnalyzerResult> findByDate(String startDate, String endDate) {
		
		Query query = new Query();
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		List<AnalyzerResult> items = analyzerResultDAO.find(query);
		return items;
	}
	
	public List<AnalyzerResult> findByCode(String code) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		List<AnalyzerResult> items = analyzerResultDAO.find(query);
		return items;
	}
	
	public List<AnalyzerResult> findByCodeAndDate(String code, String startDate, String endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		List<AnalyzerResult> items = analyzerResultDAO.find(query);
		return items;
	}

	
}
