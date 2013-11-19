package com.ml.bus.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ml.db.IBaseDB;
import com.ml.db.MongoDB;
import com.ml.task.TaskResemble;

@Service
public class TaskService {

	@Autowired
	IBaseDB baseDB;
	
	@Autowired
	TransferStockService transferStockService;

	public void retrieveStockData(List<String> stockCodes) {
		TaskResemble.retrieveStocks((MongoDB) baseDB, stockCodes, "fq", false);
	}

	public void retrieveRealStock(List<String> stockCodes) {
		TaskResemble.retrieveRealStock((MongoDB) baseDB, stockCodes);
	}

	public void retrieveDDZStock(List<String> stockCodes) {
		TaskResemble.retrieveDDZStock((MongoDB) baseDB, stockCodes);
	}

	public void retrieveShareCapital(List<String> stockCodes) {
		TaskResemble.retrieveShareCapital((MongoDB) baseDB, stockCodes);
	}

	public void transferStocks(String beginDate, String endDate, List<String> stockCodes, boolean isReal) {
		TaskResemble.transferStocks(beginDate, endDate, (MongoDB) baseDB, stockCodes, isReal);
	}
	
	public void calculate(String beginDate, String endDate, List<String> stockCodes, String strategys, boolean isReal) {
		TaskResemble.calculate(beginDate, endDate, (MongoDB) baseDB, stockCodes, strategys, isReal);
	}
}