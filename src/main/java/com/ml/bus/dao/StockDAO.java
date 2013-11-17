package com.ml.bus.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ml.db.IBaseDB;
import com.ml.model.Stock;
import com.ml.model.StockCode;
import com.ml.util.Constants;

@Repository
public class StockDAO {
	
	@Autowired
	IBaseDB baseDB;
	
	public List<Stock> findAll() {
		return baseDB.findAll(Stock.class, Constants.StockCollectionName);
	}
	
	public void delete(Stock stock) {
		baseDB.delete(stock, Constants.StockCollectionName);
	}
	
	public int count(Query query) {
		return (int) baseDB.count(query, Constants.StockCollectionName);
	}
	
	public void save(Stock stock) {
		baseDB.save(stock, Constants.StockCollectionName);
	}

	public Stock findOne(Query query) {
		return baseDB.findOne(query, Stock.class, Constants.StockCollectionName);
	}

	public List<Stock> find(Query query) {
		return baseDB.find(query, Stock.class, Constants.StockCollectionName);

	}

	public List<StockCode> findAllStockCodes() {
		return baseDB.findAll(StockCode.class, Constants.StockCodeCollectionName);
	}

}
