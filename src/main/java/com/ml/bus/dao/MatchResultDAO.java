package com.ml.bus.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ml.db.IBaseDB;
import com.ml.model.MatchResult;
import com.ml.model.Stock;
import com.ml.util.Constants;

@Repository
public class MatchResultDAO {
	
	@Autowired
	IBaseDB baseDB;
	
	public List<MatchResult> findAll() {
		return baseDB.findAll(MatchResult.class, Constants.MatchResultCollectionName);
	}
	
	public void delete(MatchResult mr) {
		baseDB.delete(mr, Constants.MatchResultCollectionName);
	}
	
	public void delete(Query query) {
		baseDB.delete(query, Constants.MatchResultCollectionName);
	}
	
	public int count(Query query) {
		return (int) baseDB.count(query, Constants.MatchResultCollectionName);
	}
	
	public void save(MatchResult mr) {
		baseDB.save(mr, Constants.MatchResultCollectionName);
	}

	public Stock findOne(Query query) {
		return baseDB.findOne(query, MatchResult.class, Constants.MatchResultCollectionName);
	}

	public List<MatchResult> find(Query query) {
		return baseDB.find(query, MatchResult.class, Constants.MatchResultCollectionName);

	}

}
