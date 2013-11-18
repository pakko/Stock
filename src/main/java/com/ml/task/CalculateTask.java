package com.ml.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.strategy.Context;

public class CalculateTask implements Runnable {
	private static final Logger logger = LoggerFactory.getLogger(CalculateTask.class);

	private Context context;
	private List<String> stockCodes;
	private List<String> dataList;
	
	public CalculateTask(List<String> stockCodes, List<String> dataList, Context context) {
		this.stockCodes = stockCodes;
		this.dataList = dataList;
		this.context = context;
	}
	
	@Override
	public void run() {
		long start = System.currentTimeMillis();
		logger.info("begin to run calculate: " + context.getStrategy().getClass().getSimpleName()
				+ ", size: " + dataList.size());
		try{
			for (String date : dataList) {
				Map<Integer, Integer> stats = new HashMap<Integer, Integer>();

				for (String stockCode : stockCodes) {
					int res = context.calculate(stockCode, date);
					
					Integer tmp = stats.get(res);
					if(tmp == null) {
						tmp = new Integer(0);
					}
					stats.put(res, tmp + 1);
				}
				logger.info("Date: " + date + ", stats: " + stats);
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		logger.info("cost time: " + (end - start));
	}

	
}
