package com.ml.stock;

import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
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
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy,MM,dd");
	
	private MongoDB mongodb;
	public SimpleModel(MongoDB mongodb) {
		this.mongodb = mongodb;
	}
	/*
	 * steps: 
	 * 	1) 计算1年的所有100天的换手率及涨幅
	 * 	2) start date的股价和5日，10日，20日，30日均价的比值
	 */
	public void calculateDaysOfHSL(String stockCode, Date startDate)
			throws Exception {
		Date endDate = getBeforeDate(startDate);
		String startDateStr = getFormatDate(startDate);  
		String endDateStr = getFormatDate(endDate);  
		System.out.println(startDate + ",," + endDate);
		GroupByResults<Stocks> result = getGroupByResult(stockCode, startDateStr, endDateStr);

		Iterator<Stocks> it = result.iterator();
		while(it.hasNext()) {
			Stocks stocks = it.next();
			System.out.println(stocks.getStockCode() + ", stocks size: " + stocks.getStocks().size());
			double hsl = 0.0;
			int totalSize = stocks.getStocks().size();
			int index = 0;
			Stock todayStock = null;
			for(Stock stock: stocks.getStocks()) {
				System.out.println("\t" + stock.getDate());
				hsl += stock.getChangeRate();
				index++;
				if(index == totalSize) {
					todayStock = stock;
				}
			}
			//calculate hsl and up
			hsl = hsl / totalSize;
			//calculate average price
			System.out.println(todayStock);

			System.out.println("average hsl: " + hsl);

		}
	}
	
	private String getFormatDate(Date date) {
		date.setMonth(date.getMonth() - 1);
		String str = "new Date(" + sdf.format(date) + ")";  
		return str;
	}
	
	
	/*
     * 
     * db.stocks.group({
		    "key" : {"_id" : true},
		    "initial" : {"stocks":[]},
		    "condition" : {"_id" : "cn_002306"},
		    "$reduce" : function(doc, prev) {
		        for (var k in doc.stocks) {
		            if (doc.stocks[k]['date'] > new Date(2013, 9, 1)) {
		                prev.stocks.push(doc.stocks[k]);
		            }
		        }
		    },
			"finalize" : function(prev) {
		        prev.count=prev.stocks.length;
		    }
		});
     */
	private GroupByResults<Stocks> getGroupByResult(String stockCode, String startDateStr, String endDateStr) {
		Criteria criteria = Criteria.where("_id").is(stockCode);  
        GroupBy groupBy = new GroupBy("_id");
        groupBy.initialDocument("{\"stocks\":[]}");
		String reduceFunction = "function(doc, prev) { "+
	        "for (var k in doc.stocks) {"+
	        "   if (doc.stocks[k]['date'] >= " + endDateStr +
	        "		&& doc.stocks[k]['date'] <= " + startDateStr + ") {"+
	        "        prev.stocks.push(doc.stocks[k]);"+
	        "    }"+
	        "}}";
		groupBy.reduceFunction(reduceFunction);
		GroupByResults<Stocks> result = mongodb.group(criteria, Constants.stockCollectionName, groupBy, Stocks.class);
		return result;
	}
	
	//get the date before 100 days
	private Date getBeforeDate(Date date) {
		Calendar nowCal = Calendar.getInstance();
		nowCal.setTime(date);
		nowCal.set(Calendar.DATE, nowCal.get(Calendar.DATE) - Constants.splitDays);

		return nowCal.getTime();
	}

	public static void main(String[] args) throws Exception {
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		
		String stockCode = "cn_002306";
		SimpleModel sm = new SimpleModel(mongodb);
		sm.calculateDaysOfHSL(stockCode, new Date());
	}
	
}
