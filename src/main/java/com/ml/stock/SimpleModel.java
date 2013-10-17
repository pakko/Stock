package com.ml.stock;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

import org.springframework.data.mongodb.core.mapreduce.GroupBy;
import org.springframework.data.mongodb.core.mapreduce.GroupByResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.util.Constants;

public class SimpleModel implements Model {

	/*
	 * 1. 100个交易日内的换手率，超过300%，也就是说在这100天的平均换手率超过3% (平均换手率：100天总换手率/100)
	 * 2.对比再前100交易日的平均换手率，这100天超过了5倍以上
	 * 3.这100天内从100天到今天股价涨幅不超过50%
	 * 4.目前股价在在5日，10日，20日，30日均线附近，也就是说5日，10日，20日，30日的均价几乎相同，相差在5%以内
	 * 
	 */
	private static SimpleDateFormat sdf;
	static {
		String pattern = "yyyy-MM-dd";
		TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");
		sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timeZone);
	}
	
	public static List<String[]> split(MongoDB mongodb, String stockCode, String startDateStr)
			throws Exception {
		Date startDate = sdf.parse(startDateStr);
		Date endDate = getBeforeDate(startDate);
		System.out.println(startDate+ "-" + endDate);
		Query query = new Query();
		query.addCriteria(Criteria.where("stockCode").is(stockCode));
		query.addCriteria(Criteria.where("stocks.date").gte(endDate).lte(startDate));

		
		Criteria criteria = Criteria.where("_id").is(stockCode);  
        GroupBy groupBy = new GroupBy("_id");
        groupBy.initialDocument("{}");
        String finalizeFunction = "function(prev) {" +
	        "for (var k in prev.stocks) {" +
	        "   if (prev.stocks[k]['changeRate'] > 1) {" +
	        "        delete prev.stocks[k];" +
	        "    }" +
	        "}}";
        
        /*
         * 
         * db.stocks.group({
    "key" : {"_id" : true},
    "initial" : {"person":[]},
    "condition" : {"_id" : "cn_002306"},
    "$reduce" : function(doc, prev) {
        for (var k in doc.stocks) {
            if (doc.stocks[k]['changeRate'] > 10) {
                prev.person.push(doc.stocks[k]);
            }
        }
    },
	"finalize" : function(prev) {
        prev.count=prev.person.length;
    }
});
         */
		groupBy.finalizeFunction(finalizeFunction);
		String reduceFunction = "function(obj,prev){prev.stocks = obj.stocks;}";
		groupBy.reduceFunction(reduceFunction);
		//List<Stocks> results = mongodb.find(query, Stocks.class, Constants.stockCollectionName);
		GroupByResults<Stock> result = mongodb.group(criteria, Constants.stockCollectionName, groupBy, Stock.class);
		//System.out.println("size: " + results.get(0).getStocks().size() + "----" + results);
		System.out.println(result.getCount());
		for(Stock stock: result) {
			System.out.println(stock.getDate());
		}
		return null;
	}
	

	private static Date getBeforeDate(Date date) {
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(date);
		nowCal.set(Calendar.DATE, nowCal.get(Calendar.DATE) - Constants.splitDays);

		return nowCal.getTime();
	}

	
	public static void main(String[] args) throws Exception {
		Calendar cal = Calendar.getInstance();    
		TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");
		cal.setTimeZone(timeZone);
		
		System.out.println(cal.getTime());
		
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		String stockCode = "cn_002306";
		String startDateStr = "2013-10-16";
		split(mongodb, stockCode, startDateStr);
	}
	
}
