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

public class Client {

    public static void main(String[] args) throws FileNotFoundException, IOException {
    	String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		String stockCode = "sh600432";
		String theDate = "2014-05-09";

        //选择并创建需要使用的策略对象
        Strategy strategy = new StrategyF(mongodb, false);
        //创建环境
        Context context = new Context(strategy);
        //计算
        //context.calculate(stockCode, theDate);
        
        List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
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
        System.out.println("stats: " + stats);
    }

}