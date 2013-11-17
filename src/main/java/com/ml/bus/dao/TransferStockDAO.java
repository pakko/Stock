package com.ml.bus.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ml.db.IBaseDB;
import com.ml.model.ScenarioResult;
import com.ml.util.Constants;

@Repository
public class TransferStockDAO {
	
	@Autowired
	IBaseDB baseDB;
	
	public List<ScenarioResult> findAll() {
		return baseDB.findAll(ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	public void delete(Query query) {
		baseDB.delete(query, Constants.ScenarioResultCollectionName);
	}
	
	public List<ScenarioResult> find(Query query) {
		return baseDB.find(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	

}
