package com.ml.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class Main {
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
	    
		List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
		dates = DateUtil.truncateDateList(dates, beginDate, Constants.BaseDays);
		List<List<String>> dataList = DateUtil.splitList(dates, 50);
		ExecutorService executor = Executors.newFixedThreadPool(dataList.size());
		for (List<String> data : dataList) {
			CalculateTask ct = new CalculateTask(mongodb, stockCodes, data);
			executor.submit(ct);
		}
		executor.shutdown();
	}
}
