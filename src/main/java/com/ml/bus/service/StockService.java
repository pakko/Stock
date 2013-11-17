package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.StockDAO;
import com.ml.model.Stock;
import com.ml.util.DateUtil;
import com.ml.util.Pagination;

@Service
public class StockService {

	@Autowired
	StockDAO stockDAO;

	public Pagination findByPageAndStockCode(Pagination pager, String code) {
		int limitSize = pager.getLimitSize();

		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		
		prePaginationSet(pager, query, limitSize);
		List<Stock> items = stockDAO.find(query);
		int totalCount = stockDAO.count(query);
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
	
	private void postPaginationSet(Pagination pager, List<Stock> items, 
			int totalCount, int limitSize) {
		int totalPage = (int)(totalCount / limitSize) + 1;
		if((totalCount % limitSize) == 0) {
			totalPage = totalPage - 1;
		}
		pager.setItems(items);
		pager.setTotalCount(totalCount);
		pager.setTotalPage(totalPage);
		
	}
	
	public void save(Stock stock) {
		stockDAO.save(stock);
	}

	public Pagination findByPageAndStockCodeAndDate(Pagination pager,
			String code, String startDate, String endDate) {
		int limitSize = pager.getLimitSize();
		
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(code));
		query.addCriteria(Criteria.where("date").gte(DateUtil.getMilliseconds(startDate))
			.lte(DateUtil.getMilliseconds(endDate)));
		
		prePaginationSet(pager, query, limitSize);
		List<Stock> items = stockDAO.find(query);
		int totalCount = stockDAO.count(query);
		postPaginationSet(pager, items, totalCount, limitSize);
		
		return pager;
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
		List<Stock> stocks = stockDAO.find(query);
		
		if(dates.size() == stocks.size())
			return true;
		return false;
	}
	
}
