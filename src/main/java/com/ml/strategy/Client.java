package com.ml.strategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class Client {

    public static void main(String[] args) throws FileNotFoundException, IOException {
    	String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		String stockCode = "sz002542";
		String theDate = "2014-05-09";

        //选择并创建需要使用的策略对象
        Strategy strategy = new StrategyF(mongodb, false);
        //创建环境
        Context context = new Context(strategy);
        //计算
        List<String> dates = DateUtil.getWorkingDays("2014-01-01", "2014-08-30");
        for(String date: dates) {
        	context.calculate(stockCode, date);
        }
        
        
        /*List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		System.out.println("Corp code size: " + stockCodes.size());
		Map<Integer, Integer> stats = new HashMap<Integer, Integer>();
        for (String code : stockCodes) {
			int res = context.calculate(code, theDate);
			Integer tmp = stats.get(res);
			if(tmp == null) {
				tmp = new Integer(0);
			}
			stats.put(res, tmp + 1);
		}
        System.out.println("stats: " + stats);*/
    }

}