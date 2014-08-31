package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.model.AnalyzerResult;
import com.ml.model.MatchResult;
import com.ml.model.RealStock;
import com.ml.model.ScenarioResult;
import com.ml.model.ShareCapital;
import com.ml.model.Stock;
import com.ml.util.Constants;
import com.ml.util.DateUtil;

public class AnalyzerDataTask implements Runnable {

	private static final Logger logger = LoggerFactory.getLogger(AnalyzerDataTask.class);

	private MongoDB mongodb;
	
	public AnalyzerDataTask(MongoDB mongodb) {
		this.mongodb = mongodb;
	}
	

	@Override
	public void run() {
		
	}
	
	private List<MatchResult> getMatchResultByStrategy(String strategy) {
		Query query = new Query();
		query.addCriteria(Criteria.where("strategy").is(strategy));
		return mongodb.find(query, MatchResult.class, Constants.MatchResultCollectionName);
	}
	
	private Map<String, String> slected;
	private void init() {
		slected = new HashMap<String, String>();
		slected.put("sh600395", "2014-04-28");
		slected.put("sz000402", "2014-05-07");
		slected.put("sz000155", "2014-04-01");
		slected.put("sz002211", "2014-04-24");
		slected.put("sh600618", "2014-05-06");
		slected.put("sz002458", "2014-05-14");
		slected.put("sz000877", "2014-05-14");
		slected.put("sh601001", "2014-05-30");
		slected.put("sh601958", "2014-06-06");
		slected.put("sz000426", "2014-06-06");
		slected.put("sh600456", "2014-06-23");
		slected.put("sz002307", "2014-06-23");
		slected.put("sz002490", "2014-06-24");
		slected.put("sz002542", "2014-06-25");
		slected.put("sh600503", "2014-07-09");
		slected.put("sz002136", "2014-07-18");
		slected.put("sz000962", "2014-07-18");
		slected.put("sz002542", "2014-06-25");
		slected.put("sh600259", "2014-07-31");
		slected.put("sh600311", "2014-07-31");
		slected.put("sh600497", "2014-08-01");
		slected.put("sh600687", "2014-08-01");
		slected.put("sz000777", "2014-08-04");
		slected.put("sz000930", "2014-08-05");
		slected.put("sh600307", "2014-08-06");
		slected.put("sz000878", "2014-08-08");
		slected.put("sz002552", "2014-08-08");
		slected.put("sz000736", "2014-08-12");
		slected.put("sh601918", "2014-08-13");
		slected.put("sz000993", "2014-08-13");
		slected.put("sz000059", "2014-08-19");
		slected.put("sz002246", "2014-08-19");
		slected.put("sh600408", "2014-08-19");
		slected.put("sz000970", "2014-08-20");
		slected.put("sh600397", "2014-08-20");
		slected.put("sz002247", "2014-08-21");
		slected.put("sz002062", "2014-08-22");
		slected.put("sz000506", "2014-08-22");
		slected.put("sh600157", "2014-08-26");
		slected.put("sz002611", "2014-08-27");
		slected.put("sh600010", "2014-08-28");
		slected.put("sz002163", "2014-08-29");
	}
	
	    
	//for old code didn't store the slected date, so get data from the log to store this date.
	private void tranlate() throws ParseException {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd"); 
		List<MatchResult> results = getMatchResultByStrategy("StrategyF");
		for(MatchResult mr: results) {
			mongodb.delete(mr, Constants.MatchResultCollectionName);
			
			String dateStr = slected.get(mr.getCode());
			if(dateStr == null)
				continue;
			
		    Date ctime = formatter.parse(dateStr);
		    mr.setFlyDate(ctime.getTime());
		    mr.setDate(mr.getDate());
		    
		    
		    System.out.println(mr);
		    mongodb.save(mr, Constants.MatchResultCollectionName);

		}
	}
	
	private int getLtpArea(double ltp) {
		if(ltp <= 30)	//small
			return 1;
		else if(ltp > 30 && ltp <= 50)	//small
			return 2;
		else if(ltp > 50 && ltp <= 100)	//small
			return 3;
		else if(ltp > 100 && ltp <= 150)	//medium
			return 4;
		else if(ltp > 150 && ltp <= 200)	//medium
			return 5;
		else if(ltp > 200 && ltp <= 300)	//medium
			return 6;
		else if(ltp > 300)		//big
			return 7;
		return 0;
	}
	
	private int getPriceArea(double price) {
		if(price <= 7)	//small
			return 1;
		else if(price > 7 && price <= 15)	//small
			return 2;
		else if(price > 15 && price <= 25)	//small
			return 3;
		else if(price > 25 && price <= 50)	//medium
			return 4;
		else if(price > 50)	//big
			return 5;
		return 0;
	}
	
	private void analyze() {
		List<MatchResult> results = getMatchResultByStrategy("StrategyF");
		for(MatchResult mr: results) {
			/*
			 * 10,20,100,120天前后换手率以及对比倍数
				是否now>5>10>20>30均价
				5,10,20,30均价斜率
				5天5日均价斜率以及手否增加
				
				流通市值30亿以下
				动态市盈率
				当前股价是否7元以下
				与大盘走势是否相当
			 */
			String code = mr.getCode();
			long date = mr.getFlyDate();
			DateTime before5 = DateUtil.getIntervalWorkingDay(date, 5, false);
			DateTime before10 = DateUtil.getIntervalWorkingDay(date, 10, false);
			DateTime before20 = DateUtil.getIntervalWorkingDay(date, 20, false);
			DateTime before30 = DateUtil.getIntervalWorkingDay(date, 30, false);
			DateTime before60 = DateUtil.getIntervalWorkingDay(date, 60, false);
			DateTime before120 = DateUtil.getIntervalWorkingDay(date, 120, false);
			DateTime before250 = DateUtil.getIntervalWorkingDay(date, 250, false);
			
			//sr
			ScenarioResult sr = getSR(code, date);
			if(sr == null) {
				System.out.println(code + ": " + DateUtil.getStrByMilliseconds(date));
				continue;
			}
			ScenarioResult sr5 = getSR(code, DateUtil.getMilliseconds(before5));
			ScenarioResult sr10 = getSR(code, DateUtil.getMilliseconds(before10));
			ScenarioResult sr20 = getSR(code, DateUtil.getMilliseconds(before20));
			ScenarioResult sr30 = getSR(code, DateUtil.getMilliseconds(before30));
			ScenarioResult sr60 = getSR(code, DateUtil.getMilliseconds(before60));
			ScenarioResult sr120 = getSR(code, DateUtil.getMilliseconds(before120));
			ScenarioResult sr250 = getSR(code, DateUtil.getMilliseconds(before250));
			
			//sr hsl
			double sr_hsl5 = sr.getHsl5();
			double sr_hsl10 = sr.getHsl10();
			double sr_hsl20 = sr.getHsl20();
			double sr_hsl30 = sr.getHsl30();
			double sr_hsl60 = sr.getHsl60();
			double sr_hsl120 = sr.getHsl120();
			double sr_hsl250 = sr.getHsl250();
			
			double sr5_hsl5 = (sr5 != null) ? sr5.getHsl5() : 0;
			double sr10_hsl10 = (sr10 != null) ? sr10.getHsl10() : 0;
			double sr20_hsl20 = (sr20 != null) ? sr20.getHsl20() : 0;
			double sr30_hsl30 = (sr30 != null) ? sr30.getHsl30() : 0;
			double sr60_hsl60 = (sr60 != null) ? sr60.getHsl60() : 0;
			double sr120_hsl120 = (sr120 != null) ? sr120.getHsl120() : 0;
			double sr250_hsl250 = (sr250 != null) ? sr250.getHsl250() : 0;
			
			//hsl ratio
			double hsl5_ratio = sr_hsl5 / sr5_hsl5;
			double hsl10_ratio = sr_hsl10 / sr10_hsl10;
			double hsl20_ratio = sr_hsl20 / sr20_hsl20;
			double hsl30_ratio = sr_hsl30 / sr30_hsl30;
			double hsl60_ratio = sr_hsl60 / sr60_hsl60;
			double hsl120_ratio = sr_hsl120 / sr120_hsl120;
			double hsl250_ratio = sr_hsl250 / sr250_hsl250;
			
			//ma
			double price = sr.getNowPrice();
			double ma5 = sr.getMa5();
			double ma10 = sr.getMa10();
			double ma20 = sr.getMa20();
			double ma30 = sr.getMa30();
			double ma60 = sr.getMa60();
			double ma120 = sr.getMa120();
			double ma250 = sr.getMa250();
			
			//is up tunnel
			boolean isAvgPriceGood = (price >= ma5) ? ((ma5 >= ma10) ? ( (ma10 >= ma20) ? (ma20 >= ma30) : false) : false ) : false;
			
			//ma ratio
			double baseMa = (ma5 + ma10 + ma20+ ma30) / 4;
			double ma5_ratio = (ma5 - baseMa) / baseMa;
			double ma10_ratio = (ma10 - baseMa) / baseMa;
			double ma20_ratio = (ma20 - baseMa) / baseMa;
			double ma30_ratio = (ma30 - baseMa) / baseMa;
			
			//up
			double up5 = sr.getUp5() * 5;
			double up10 = sr.getUp10() * 10;
			double up20 = sr.getUp20() * 20;
			double up30 = sr.getUp30() * 30;
			
			//is gradient up
			boolean isGradUp = up5 > 0;
			
			//ltsz area
			double ltp = sr.getLtp();
			double ltsz = ltp / 10000 * price;
			int ltszArea = getLtpArea(ltsz);
			
			//price area
			int priceArea = getPriceArea(price);

			String strategy = mr.getStrategy();
			
			AnalyzerResult ar = new AnalyzerResult(code, date, strategy, sr_hsl5, sr5_hsl5,
					sr_hsl10, sr10_hsl10, sr_hsl20, sr20_hsl20, sr_hsl30, sr30_hsl30,
					sr_hsl60, sr60_hsl60, sr_hsl120, sr120_hsl120, sr_hsl250, sr250_hsl250,
					hsl5_ratio, hsl10_ratio, hsl20_ratio, hsl30_ratio, hsl60_ratio,
					hsl120_ratio, hsl250_ratio, isAvgPriceGood, ma5_ratio, ma10_ratio, ma20_ratio, ma30_ratio,
					up5, up10, up20, up30, isGradUp, ltszArea, 0, priceArea, 0);
			
			mongodb.save(ar, "analyzerResult");
		}
	}
	
	protected ScenarioResult getSR(String stockCode, long date) {
		Query query = new Query();
		query.addCriteria(Criteria.where("code").is(stockCode));
		query.addCriteria(Criteria.where("date").is(date));
		return mongodb.findOne(query, ScenarioResult.class, Constants.ScenarioResultCollectionName);
	}
	
	public static void main(String[] args) throws Exception {
		String confFile = Constants.DefaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		AnalyzerDataTask adt = new AnalyzerDataTask(mongodb);
		//adt.init();
		//adt.tranlate();
		adt.analyze();
	}
}
