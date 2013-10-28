package com.ml.strategy;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

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
		
		String stockCode = "cn_600803";
		String theDate = "2013-10-23";

        //选择并创建需要使用的策略对象
        Strategy strategy = new StrategyA(mongodb);
        //创建环境
        Context context = new Context(strategy);
        //计算
        context.calculate(stockCode, theDate);
    }

}