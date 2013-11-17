package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.TransferStockDAO;
import com.ml.model.ScenarioResult;
import com.ml.util.DateUtil;

@Service
public class TransferStockService {

	@Autowired
	TransferStockDAO transferStockDAO;


	public List<ScenarioResult> findAll() {
		return transferStockDAO.findAll();
	}
	
	public void clearRealTransfer() {
		Query query = new Query();
		query.addCriteria(Criteria.where("isReal").is(true));
		transferStockDAO.delete(query);
	}
	
	public boolean checkRetrieved(List<String> stockCodes, String startDate,
			String endDate) {
		List<String> dates = DateUtil.getWorkingDays(startDate, endDate);
		
		//just check one is enough
		String code = "sh600000";
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		List<ScenarioResult> srs = transferStockDAO.find(query);
		
		if(dates.size() == srs.size())
			return true;
		return false;
	}
}