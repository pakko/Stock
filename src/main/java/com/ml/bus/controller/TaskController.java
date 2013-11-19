package com.ml.bus.controller;


import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ml.bus.service.MatchResultService;
import com.ml.bus.service.MemoryService;
import com.ml.bus.service.StockService;
import com.ml.bus.service.TaskService;
import com.ml.bus.service.TransferStockService;


@Controller
@RequestMapping(value = "/task")
public class TaskController {
	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
	
	@Autowired
    private TaskService taskService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Autowired
	TransferStockService transferStockService;

    @Autowired
    private StockService stockService;
    
    @Autowired
    private MatchResultService matchResultService;
    
    @RequestMapping(value = "/retrieve", method = RequestMethod.GET)
    public @ResponseBody String retrieve(
    		@RequestParam(value = "type", required = true) String type) {
    	
    	List<String> stockCodes = memoryService.getStockCodes();
    	
    	logger.info("begin to retrieve stock data, type: " + type);
    	if(type.equals("history")) {
    		taskService.retrieveStockData(stockCodes);
    	}
    	else if(type.equals("real")) {
    		taskService.retrieveRealStock(stockCodes);
    	}
    	else if(type.equals("sc")) {
    		taskService.retrieveShareCapital(stockCodes);
    	}
    	else if(type.equals("ddx")) {
    		taskService.retrieveDDZStock(stockCodes);
    	}
    	logger.info("end of retrieving stock data");
    	
    	return "{\"success\": \"ok\"}";
    	
    }
    
    @RequestMapping(value = "/transfer", method = RequestMethod.GET)
    public @ResponseBody String transfer(
    		@RequestParam(value = "type", required = true) String type,
    		@RequestParam(value = "startDate", required = true) String startDate,
    		@RequestParam(value = "endDate", required = true) String endDate) {
    	
    	List<String> stockCodes = memoryService.getStockCodes();
    	logger.info("begin to transfer stock data, type: " + type + " ,startDate: " + startDate + " ,endDate: " + endDate);
    	if(type.equals("history")) {
    		//check whether transfered
    		transferStockService.clearRealTransfer();
    		boolean transfered = transferStockService.checkRetrieved(stockCodes, startDate, endDate);
    		logger.info("check transfered: " + transfered);
    		if(!transfered)
    			taskService.transferStocks(startDate, endDate, stockCodes, false);
    	}
    	else if(type.equals("real")) {
    		transferStockService.clearRealTransfer();
    		taskService.transferStocks(startDate, endDate, stockCodes, true);
    	}
    	logger.info("end of transfering stock data");
    	
    	return "{\"success\": \"ok\"}";
    	
    }
    
    @RequestMapping(value = "/calculate", method = RequestMethod.GET)
    public @ResponseBody String calculate(
    		@RequestParam(value = "type", required = false) String type,
    		@RequestParam(value = "startDate", required = true) String startDate,
    		@RequestParam(value = "endDate", required = true) String endDate,
    		@RequestParam(value = "strategys", required = true) String strategys) {
    	
    	logger.info("begin to calculate stock data, type: " + type + " ,startDate: " + startDate + " ,endDate: " + endDate);
    	List<String> stockCodes = memoryService.getStockCodes();
    	//clear calculated
    	matchResultService.clearMatchResult(startDate, endDate);
    	
    	boolean isReal = false;
    	if(type.equals("real")) {
    		isReal = true;
    	}
    	taskService.calculate(startDate, endDate, stockCodes, strategys, isReal);
    	logger.info("end of calculating stock data");
    	
    	return "{\"success\": \"ok\"}";
    	
    }
    
    @RequestMapping(value = "/oneClickCalculate", method = RequestMethod.GET)
    public @ResponseBody String oneClickCalculate(
    		@RequestParam(value = "type", required = true) String type,
    		@RequestParam(value = "startDate", required = true) String startDate,
    		@RequestParam(value = "endDate", required = true) String endDate) {
    	
    	List<String> stockCodes = memoryService.getStockCodes();
    	String strategys = memoryService.getStrategys();
    	
    	logger.info("begin to oneclick calculate stock data, type: " + type + " ,startDate: " + startDate + " ,endDate: " + endDate);
    	if(type.equals("history")) {
    		//1, check whether retrieved
    		boolean retrieved = stockService.checkRetrieved(stockCodes, startDate, endDate);
    		logger.info("check retrieved: " + retrieved);
    		if(!retrieved)
    			taskService.retrieveStockData(stockCodes);
    		//2, clear real transfered
    		transferStockService.clearRealTransfer();
    		//3, check whether transfered
    		boolean transfered = transferStockService.checkRetrieved(stockCodes, startDate, endDate);
    		logger.info("check transfered: " + transfered);
    		if(!transfered)
    			taskService.transferStocks(startDate, endDate, stockCodes, false);
    		//4, clear calculated
        	matchResultService.clearMatchResult(startDate, endDate);
        	//5, calculate
        	taskService.calculate(startDate, endDate, stockCodes, strategys, false);
    	}
    	else if(type.equals("real")) {
    		//1, retrieve
    		taskService.retrieveRealStock(stockCodes);
    		//2, clear real transfered
    		transferStockService.clearRealTransfer();
    		//3, transfer
    		taskService.transferStocks(startDate, endDate, stockCodes, true);
    		//4, clear calculated
        	matchResultService.clearMatchResult(startDate, endDate);
        	//5, calculate
        	taskService.calculate(startDate, endDate, stockCodes, strategys, true);
    	}
    	
    	logger.info("end of oneclick calculating stock data");
    	
    	return "{\"success\": \"ok\"}";
    	
    }
}
