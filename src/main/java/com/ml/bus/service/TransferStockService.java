package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.TransferStockDAO;
import com.ml.model.ScenarioResult;
import com.ml.util.DateUtil;
import com.ml.util.Pagination;

@Service
public class TransferStockService {

	@Autowired
	TransferStockDAO transferStockDAO;

	public ScenarioResult findByStockCodeAndDate(String code, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").is(date));
		List<ScenarioResult> srs = transferStockDAO.find(query);
		if(srs.size() > 0)
			return srs.get(0);
		return null;
	}
	
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
	
	public Pagination findByPageAndStockCode(Pagination pager, String code) {
		int limitSize = pager.getLimitSize();

		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		
		prePaginationSet(pager, query, limitSize);
		List<ScenarioResult> items = transferStockDAO.find(query);
		int totalCount = transferStockDAO.count(query);
		postPaginationSet(pager, items, totalCount, limitSize);
		return pager;
	}
	
	private void prePaginationSet(Pagination pager, Query query, int limitSize) {
		int startIndex = pager.getStartIndex();
		
		if(pager.getSortOrder().equals("desc")){
			query.with(new Sort(new Sort.Order(Direction.DESC, pager.getSortField())));
		}
		else if(pager.getSortOrder().equals("asc")){
			query.with(new Sort(new Sort.Order(Direction.ASC, pager.getSortField())));
		}
		query = query.skip(startIndex).limit(limitSize);
	}
	
	private void postPaginationSet(Pagination pager, List<ScenarioResult> items, 
			int totalCount, int limitSize) {
		int totalPage = (int)(totalCount / limitSize) + 1;
		if((totalCount % limitSize) == 0) {
			totalPage = totalPage - 1;
		}
		pager.setItems(items);
		pager.setTotalCount(totalCount);
		pager.setTotalPage(totalPage);
		
	}

	public Pagination findByPageAndStockCodeAndDate(Pagination pager,
			String code, String startDate, String endDate) {
		int limitSize = pager.getLimitSize();
		
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		prePaginationSet(pager, query, limitSize);
		List<ScenarioResult> items = transferStockDAO.find(query);
		int totalCount = transferStockDAO.count(query);
		postPaginationSet(pager, items, totalCount, limitSize);
		
		return pager;
	}
}