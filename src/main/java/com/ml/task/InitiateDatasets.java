package com.ml.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.qevent.QueueListenerManager;
import com.ml.qevent.TransferDataListener;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class InitiateDatasets {
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
		String endDate = "2013-10-20";
		
		// get data for transferDataListener
		List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
		List<List<String>> dateList = DateUtil.splitList(dates, 50);
		ExecutorService transferDataService = Executors.newFixedThreadPool(dateList.size());
		// add listener
		QueueListenerManager manager = new QueueListenerManager();
        manager.addQueueListener(new TransferDataListener(mongodb, stockCodes, dateList, transferDataService));
        
        // get dates for retrieveDataTask
		List<String[]> splitDates = DateUtil.getSplitDates(beginDate, endDate, Constants.SplitDays);
		ExecutorService retrieveDataService = Executors.newFixedThreadPool(splitDates.size());
	    for (String[] splitDate : splitDates) {
			RetrieveDataTask rdt = new RetrieveDataTask(mongodb, stockCodes, splitDate, manager);
			retrieveDataService.submit(rdt);
		}
	    retrieveDataService.shutdown();
	    
		
	}
}
