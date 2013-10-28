package com.ml.task;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


import com.ml.db.MongoDB;
import com.ml.util.DateUtil;

public class InitiateDatasets {
	
	public static void retrieveStocks(MongoDB mongodb, List<String> stockCodes, 
			String type, boolean isLatest) {
		List<List<String>> stockCodeList = DateUtil.splitList(stockCodes, 200);
		ExecutorService retrieveDataExecutor = Executors.newFixedThreadPool(stockCodeList.size());
	    for (List<String> subStockCodes: stockCodeList) {
			RetrieveStockDataTask rsdt = new RetrieveStockDataTask(mongodb, subStockCodes, type, isLatest);
			retrieveDataExecutor.submit(rsdt);
		}
	    retrieveDataExecutor.shutdown();
	    
	    waitForComplete(retrieveDataExecutor, 60 * 2);
	}
	
	public static void retrieveShareCapital(MongoDB mongodb, List<String> stockCodes) {
		List<List<String>> stockCodeList = DateUtil.splitList(stockCodes, 200);
		ExecutorService retrieveDataExecutor = Executors.newFixedThreadPool(stockCodeList.size());
	    for (List<String> subStockCodes: stockCodeList) {
	    	RetrieveSCDataTask rscdt = new RetrieveSCDataTask(mongodb, subStockCodes);
			retrieveDataExecutor.submit(rscdt);
		}
	    retrieveDataExecutor.shutdown();
	    
	    waitForComplete(retrieveDataExecutor, 60 * 2);
	}
	
	public static void transferStocks(String beginDate, String endDate,
			MongoDB mongodb, List<String> stockCodes) {
		// get data for transferDataTask
		List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
		List<List<String>> dateList = DateUtil.splitList(dates, 50);
		ExecutorService transferDataExecutor = Executors.newFixedThreadPool(dateList.size());
		for (List<String> date : dateList) {
			TransferDataTask tdt = new TransferDataTask(mongodb, stockCodes, date);
			transferDataExecutor.submit(tdt);
		}
		transferDataExecutor.shutdown();
		waitForComplete(transferDataExecutor, 60 * 2);
	}
	
	private static void waitForComplete(ExecutorService executor, int seconds) {
		try {  
            boolean loop = true;  
            while(loop) {    //等待所有任务完成  
                loop = !executor.awaitTermination(seconds, TimeUnit.SECONDS);
            }
        } catch (InterruptedException e) {  
            e.printStackTrace();  
        }  
	}
}
