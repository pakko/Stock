package com.ml.task;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;







import org.apache.commons.io.FileUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import com.ml.db.MongoDB;
import com.ml.util.Constants;
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
		if(dates.size() <= 0)
			return;
		List<List<String>> dateList = DateUtil.splitList(dates, 50);
		ExecutorService transferDataExecutor = Executors.newFixedThreadPool(dateList.size());
		for (List<String> date : dateList) {
			TransferDataTask tdt = new TransferDataTask(mongodb, stockCodes, date);
			transferDataExecutor.submit(tdt);
		}
		transferDataExecutor.shutdown();
		waitForComplete(transferDataExecutor, 60 * 2);
	}
	
	public static void retrieveDDZStock(MongoDB mongodb, List<String> stockCodes) {
		List<List<String>> stockCodeList = DateUtil.splitList(stockCodes, 200);
		ExecutorService retrieveDataExecutor = Executors.newFixedThreadPool(stockCodeList.size());
	    for (List<String> subStockCodes: stockCodeList) {
	    	RetrieveDdzStockDataTask rscdt = new RetrieveDdzStockDataTask(mongodb, subStockCodes);
			retrieveDataExecutor.submit(rscdt);
		}
	    retrieveDataExecutor.shutdown();
	    
	    waitForComplete(retrieveDataExecutor, 60 * 2);
	}
	
	public static void retrieveRealStock(MongoDB mongodb, List<String> stockCodes) {
		List<List<String>> stockCodeList = DateUtil.splitList(stockCodes, 200);
		ExecutorService retrieveDataExecutor = Executors.newFixedThreadPool(stockCodeList.size());
	    for (List<String> subStockCodes: stockCodeList) {
	    	RetrieveRealStockDataTask rscdt = new RetrieveRealStockDataTask(mongodb, subStockCodes);
			retrieveDataExecutor.submit(rscdt);
		}
	    retrieveDataExecutor.shutdown();
	    
	    waitForComplete(retrieveDataExecutor, 60 * 2);
	}
	
	public static Map<String, String> getStockCodes() throws IOException {
		String content = FileUtils.readFileToString(new File(Constants.CECodesFile));
		ObjectMapper objectMapper = new ObjectMapper();
		List<List<Object>> lists = objectMapper.readValue(content, 
					new TypeReference<List<List<Object>>>(){});
		Map<String, String> map = new HashMap<String, String>(lists.size());
		for(List<Object> list: lists) {
			map.put((String) list.get(0), (String) list.get(1));
		}
		return map;
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
