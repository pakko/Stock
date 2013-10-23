package com.ml.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.qevent.QueueListenerManager;
import com.ml.qevent.TransferDataListener;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class InitiateDatasets {
	
	public static void retrieveStocks(String beginDate, String endDate,
			MongoDB mongodb, List<String> stockCodes) {
		// get dates for retrieveDataTask
		List<String[]> splitDates = DateUtil.getSplitDates(beginDate, endDate, Constants.SplitDays);
		ExecutorService retrieveDataExecutor = Executors.newFixedThreadPool(splitDates.size());
	    for (String[] splitDate : splitDates) {
			RetrieveDataTask rdt = new RetrieveDataTask(mongodb, stockCodes, splitDate);
			retrieveDataExecutor.submit(rdt);
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
	
	public static void main(String[] args) throws Exception  {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		// stock codes
		List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		System.out.println("Corp code size: " + stockCodes.size());
		
		String beginDate = "2013-01-01";
		String endDate = "2013-10-21";
		
		int cmd = 2;
		if(cmd == 1) {
			// get dates for retrieveDataTask
			List<String[]> splitDates = DateUtil.getSplitDates(beginDate, endDate, Constants.SplitDays);
			ExecutorService retrieveDataService = Executors.newFixedThreadPool(splitDates.size());
		    for (String[] splitDate : splitDates) {
				RetrieveDataTask rdt = new RetrieveDataTask(mongodb, stockCodes, splitDate);
				retrieveDataService.submit(rdt);
			}
		    retrieveDataService.shutdown();
		}
		else if(cmd == 2) {
			// get data for transferDataListener
			List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
			List<List<String>> dateList = DateUtil.splitList(dates, 50);
			ExecutorService transferDataService = Executors.newFixedThreadPool(dateList.size());
			// add listener
			QueueListenerManager manager = new QueueListenerManager();
	        manager.addQueueListener(new TransferDataListener(mongodb, stockCodes, dateList, transferDataService));
	        manager.fireWorkspaceCommand("take_retrieved_data");
		}
		
	}
}
