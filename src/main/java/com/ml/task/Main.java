package com.ml.task;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.Stock;
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
		
		String beginDate = "2013-10-21";
		String endDate = "2013-10-21";
	    
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is("cn_600000"));
		query.addCriteria(Criteria.where("date").is(DateUtil.getMilliseconds(endDate)));
		Stock stock = mongodb.findOne(query, Stock.class, Constants.StockCollectionName);
		System.out.println(stock);
		if(stock == null) {
			InitiateDatasets.retrieveStocks(beginDate, endDate, mongodb, stockCodes);
			InitiateDatasets.transferStocks(beginDate, endDate, mongodb, stockCodes);
		}
		
		int cmd = 1;
		if(cmd == 1) {
			List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
			//dates = DateUtil.truncateDateList(dates, beginDate, Constants.BaseDays);
			List<List<String>> dataList = DateUtil.splitList(dates, 50);
			System.out.println(dataList);
			ExecutorService executor = Executors.newFixedThreadPool(dataList.size());
			for (List<String> data : dataList) {
				CalculateTask ct = new CalculateTask(mongodb, stockCodes, data);
				executor.submit(ct);
			}
			executor.shutdown();
		}
		else if(cmd == 2) {
			List<MatchResult> matches = mongodb.findAll(MatchResult.class, Constants.MatchResultCollectionName + "_01");
			Collections.sort(matches, new Comparator<MatchResult>() {

				@Override
				public int compare(MatchResult o1, MatchResult o2) {
					return o1.getDate() > o2.getDate() ? 1 : (o1.getDate() == o2.getDate() ? 0 : -1);
				}
				
			});
			for(MatchResult match: matches) {
				System.out.println(match.getCode() + ": " + DateUtil.getDateByMilliseconds(match.getDate()));
			}
		}
	}
}
