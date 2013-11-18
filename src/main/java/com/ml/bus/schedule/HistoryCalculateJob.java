package com.ml.bus.schedule;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.ml.bus.service.MatchResultService;
import com.ml.bus.service.MemoryService;
import com.ml.bus.service.StockService;
import com.ml.bus.service.TaskService;
import com.ml.bus.service.TransferStockService;
import com.ml.util.ApplicationContextUtil;

public class HistoryCalculateJob extends QuartzJobBean {

	private static final Logger logger = LoggerFactory.getLogger(HistoryCalculateJob.class);
    
	private TaskService taskService;
	private MemoryService memoryService;
	private StockService stockService;
	private TransferStockService transferStockService;
	private MatchResultService matchResultService;

    @Override
	protected void executeInternal(JobExecutionContext context) {
    	try {
			ApplicationContext ctx = ApplicationContextUtil.getQuartzApplicationContext(context);
			taskService = (TaskService)ctx.getBean("taskService");
			memoryService = (MemoryService)ctx.getBean("memoryService");
			stockService = (StockService)ctx.getBean("stockService");
			transferStockService = (TransferStockService)ctx.getBean("transferStockService");
			matchResultService = (MatchResultService)ctx.getBean("matchResultService");

			List<String> stockCodes = memoryService.getStockCodes();
	    	String strategys = memoryService.getStrategys();
	    	
	    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd");  
	        String date = df.format(new Date()); 
	    	logger.info("begin to oneclick calculate stock data ,startDate: " + date + " ,endDate: " + date);
			//1, check whether retrieved
			boolean retrieved = stockService.checkRetrieved(stockCodes, date, date);
			logger.info("check retrieved: " + retrieved);
			if(!retrieved)
				taskService.retrieveStockData(stockCodes);
			//2, clear real transfered
			transferStockService.clearRealTransfer();
			//3, check whether transfered
			boolean transfered = transferStockService.checkRetrieved(stockCodes, date, date);
			logger.info("check transfered: " + transfered);
			if(!transfered)
				taskService.transferStocks(date, date, stockCodes, false);
	    	//4, clear calculated
	    	matchResultService.clearMatchResult(date, date);
	    	//5, calculate
	    	taskService.calculate(date, date, stockCodes, strategys);
	    	logger.info("end of oneclick calculating stock data");
		} catch(Exception e) {
			logger.error(e.getMessage());
			e.printStackTrace();
		}
	}
}
