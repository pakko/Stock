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

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.strategy.Context;
import com.ml.strategy.Strategy;
import com.ml.strategy.StrategyA;
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
		//List<String> stockCodes = FileUtils.readLines(new File(args[1]));
		List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		System.out.println("Corp code size: " + stockCodes.size());
				
		String beginDate = props.getProperty("beginDate");
		String endDate = props.getProperty("endDate");
		String retrieveType = props.getProperty("retrieveType");
        boolean isLatest = Boolean.valueOf(props.getProperty("isLatest"));
        
        //beginDate = "2012-01-01";
		//endDate = "2013-10-25";
		//retrieveType = "fq";	//last or fq, fq mean 'fu quan'
		//isLatest = false;		// true only get the latest one
		
        int cmd = Integer.valueOf(props.getProperty("cmd"));
        if(cmd == 1) {
        	InitiateDatasets.retrieveStocks(mongodb, stockCodes, retrieveType, isLatest);
        } else if(cmd == 2) {
        	InitiateDatasets.retrieveShareCapital(mongodb, stockCodes);
        } else if(cmd == 3) {
        	InitiateDatasets.transferStocks(beginDate, endDate, mongodb, stockCodes);
        }
        else if(cmd == 4) {
			List<String> dates = DateUtil.getWorkingDays(beginDate, endDate);
			List<List<String>> dataList = DateUtil.splitList(dates, 50);
			ExecutorService executor = Executors.newFixedThreadPool(dataList.size());
			
			//strategy model
			Strategy strategy = new StrategyA(mongodb);
			Context context = new Context(strategy);
	        
			for (List<String> data : dataList) {
				CalculateTask ct = new CalculateTask(stockCodes, data, context);
				executor.submit(ct);
			}
			executor.shutdown();
		} else if(cmd == 5) {
			List<MatchResult> matches = mongodb.findAll(MatchResult.class, Constants.MatchResultCollectionName);
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
