package com.ml.bus.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;

import com.ml.db.IBaseDB;
import com.ml.model.AnalyzerResult;
import com.ml.util.Constants;

@Repository
public class AnalyzerResultDAO {
	
	@Autowired
	IBaseDB baseDB;
	
	public List<AnalyzerResult> findAll() {
		return baseDB.findAll(AnalyzerResult.class, Constants.AnalyzerResultCollectionName);
	}
	
	public void delete(AnalyzerResult mr) {
		baseDB.delete(mr, Constants.AnalyzerResultCollectionName);
	}
	
	public void delete(Query query) {
		baseDB.delete(query, Constants.AnalyzerResultCollectionName);
	}
	
	public int count(Query query) {
		return (int) baseDB.count(query, Constants.AnalyzerResultCollectionName);
	}
	
	public void save(AnalyzerResult mr) {
		baseDB.save(mr, Constants.AnalyzerResultCollectionName);
	}

	public AnalyzerResult findOne(Query query) {
		return baseDB.findOne(query, AnalyzerResult.class, Constants.AnalyzerResultCollectionName);
	}

	public List<AnalyzerResult> find(Query query) {
		return baseDB.find(query, AnalyzerResult.class, Constants.AnalyzerResultCollectionName);
	}

}
