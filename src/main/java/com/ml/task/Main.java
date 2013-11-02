package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.io.FileUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.MatchResult;
import com.ml.model.Stock;
import com.ml.strategy.Context;
import com.ml.strategy.Strategy;
import com.ml.strategy.StrategyA;
import com.ml.strategy.StrategyB;
import com.ml.strategy.StrategyC;
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
			if(dataList.size() <= 0)
				return;
			ExecutorService executor = Executors.newFixedThreadPool(dataList.size());
			
			//strategy model
			Strategy strategy = new StrategyC(mongodb);
			Context context = new Context(strategy);
	        
			for (List<String> data : dataList) {
				CalculateTask ct = new CalculateTask(stockCodes, data, context);
				executor.submit(ct);
			}
			executor.shutdown();
        } else if(cmd == 5) {
        	InitiateDatasets.retrieveRealStock(mongodb, stockCodes);
        } else if(cmd == 6) {
        	//retrieve 20 days ddz stock
        	InitiateDatasets.retrieveDDZStock(mongodb, stockCodes);
		} else if(cmd == 7) {
			List<MatchResult> matches = mongodb.findAll(MatchResult.class, Constants.MatchResultCollectionName);
			Collections.sort(matches, new Comparator<MatchResult>() {
				@Override
				public int compare(MatchResult o1, MatchResult o2) {
					return o1.getDate() > o2.getDate() ? 1 : (o1.getDate() == o2.getDate() ? 0 : -1);
				}
			});
			Map<String, String> map = InitiateDatasets.getStockCodes();
			int i = 0;
			Map<String, List<DateTime>> stat = new HashMap<String, List<DateTime>>();
			for(MatchResult match: matches) {
				int res = compareMatch(mongodb, match.getCode(), match.getDate());
				if(res == 1) {
					//System.out.println("Good: " + match.getCode() + ": " + DateUtil.getDateByMilliseconds(match.getDate()));
					i++;
					List<DateTime> dates = stat.get(match.getCode());
					if(dates == null) {
						dates = new ArrayList<DateTime>();
					}
					dates.add(DateUtil.getDateByMilliseconds(match.getDate()));
					stat.put(match.getCode(), dates);
				}
				else {
					//System.out.println("Bad: " + match.getCode() + ": " + DateUtil.getDateByMilliseconds(match.getDate()));
				}
			}
			System.out.println("Good size: " + i );
			for(String key: stat.keySet()) {
				List<DateTime> dates = stat.get(key);
				Collections.sort(dates, new Comparator<DateTime>() {
					@Override
					public int compare(DateTime o1, DateTime o2) {
						return DateUtil.getMilliseconds(o1) > DateUtil.getMilliseconds(o2) ? -1 
								: (DateUtil.getMilliseconds(o1) == DateUtil.getMilliseconds(o2) ? 0 : 1);
					}
				});
				System.out.println(key + "("+ map.get(key.substring(2)) + "): " + dates);
			}
		}
        
	}
	private static int compareMatch(MongoDB mongodb, String stockCode, long theDate) {
		DateTime afterDate = DateUtil.getIntervalWorkingDay(theDate, 5, true);
		long afterDateSecs = DateUtil.getMilliseconds(afterDate);
		List<Stock> stockList = getQueryBetweenStocks(mongodb, stockCode, theDate, afterDateSecs);
		double nowPrice = stockList.get(0).getClose();
		for(int i = 1; i < stockList.size(); i++) {
			if(nowPrice < stockList.get(i).getClose()) {
				return 1;
			}
		}
		return 0;

	}
	
	private static List<Stock> getQueryBetweenStocks(MongoDB mongodb, String stockCode, 
			long beginDate, long endDate) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").gte(beginDate).lte(endDate));
		query.with(new Sort(new Sort.Order(Direction.ASC, "date")));
		return mongodb.find(query, Stock.class, Constants.StockCollectionName);
	}
}
