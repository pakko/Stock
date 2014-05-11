package com.ml.strategy;

import java.util.ArrayList;
import java.util.List;

import hirondelle.date4j.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.model.Stock;
import com.ml.util.DateUtil;


public class StrategyE extends AbstractStrategy {

	/* 
     	1，获取两年股价最低点
		2，最低点到1年前的股价是渐次降低（每个月的斜率是下降的，占比60%，价格幅度在5%时忽略不计时占比80%）
		2012.5-2013.5，600432：24个月中5次上升；600395：24个月中3次上升
		2011.10-2013.10,002024：24个月中10次上升
		3，当前2个月处于上升通道（月斜率是上升的）
		4，2个月换手率明显大于之前两个月换手率
		5，最近2个月大阳线数据大于3个（大阳线：涨幅5%以上）
     */
	private static final Logger logger = LoggerFactory.getLogger(StrategyE.class);

	public StrategyE(MongoDB mongodb, Boolean isReal) {
		super(mongodb, isReal);
	}
	
	private int DAYS_OF_YEAR = 365;
	private int MONTH_OF_YEAR = 12;
	private int DAYS_OF_MONTH = 30;
	
	public int calculate(String stockCode, String theDate) {
		int flag = 0;
        try{
            long theDateSecs = DateUtil.getMilliseconds(theDate);
            //1-1, 得到2年的股价
            List<ScenarioResult> twoYearSRs = getBeforeRangeSR(stockCode, theDateSecs, DAYS_OF_YEAR * 2);
            if(twoYearSRs.size() <= 0)
            	return flag;
            
            //1-2, 得到最低股价
            ScenarioResult minSR = findMinStockSR(twoYearSRs);
            		
            //2-1, 得到minSR前1年的股价
            List<ScenarioResult> oneYearSRs = getBeforeRangeSR(stockCode, minSR.getDate(), DAYS_OF_YEAR);
            int oneYearSize = oneYearSRs.size();
            if(oneYearSize < DAYS_OF_YEAR / 2)
            	return flag;
            
            int limit = oneYearSize / MONTH_OF_YEAR;
            
            //System.out.println(DateUtil.getDateByMilliseconds(oneYearSRs.get(0).getDate()));
            
            //2-2, 得到minSR前1年内每个月的涨跌幅
            List<Double> monthUpList = new ArrayList<Double>(MONTH_OF_YEAR);
            for(int i = 0; i < MONTH_OF_YEAR; i++) {
            	int startIndex = i * limit;
            	int endIndex = (MONTH_OF_YEAR - i == 1) ? (oneYearSize - 1) : (i + 1) * limit;
            	double endPrice = oneYearSRs.get(endIndex).getNowPrice();
            	double startPrice = oneYearSRs.get(startIndex).getNowPrice();
            	double diff = endPrice - startPrice;
            	
            	//System.out.println(DateUtil.getDateByMilliseconds(oneYearSRs.get(startIndex).getDate())
            	//		+ ": " + DateUtil.getDateByMilliseconds(oneYearSRs.get(endIndex).getDate()));
            	monthUpList.add(diff / startPrice);
            }
            //System.out.println(monthUpList);
            
            //2-3, 统计个数
            int downSize = 0;
            int strickDownSize = 0;
            for(double monthUp: monthUpList){
            	//价格幅度大于0的个数
            	if(monthUp > 0)
            		downSize++;
            	//价格幅度在5%的个数
            	if(monthUp * 100 > 5.0)
            		strickDownSize++;
            }
            //System.out.println(downSize + ":" + strickDownSize);
            
            //2-4, 占比40%以上时返回
            flag = 1;
            if(downSize / DAYS_OF_YEAR > 0.4)
            	return flag;
            
            //2-5, 价格幅度在5%时忽略不计时占比80%
            flag = 2;
            if(strickDownSize / DAYS_OF_YEAR > 0.2)
            	return flag;
            
            //3-1, 得到min后两个月的股价
            int howMany = 2;
            List<ScenarioResult> twoMonthSRs = getAfterRangeSR(stockCode, minSR.getDate(), DAYS_OF_MONTH * howMany);
            int twoMonthSize = twoMonthSRs.size();
            if(twoMonthSize < DAYS_OF_MONTH)
            	return flag;
            
            //System.out.println(twoMonthSize);
            limit = twoMonthSize / howMany;
            
            //3-2, 得到min后两个月内每个月的涨跌幅
            List<Double> upList = new ArrayList<Double>(howMany);
            for(int i = 0; i < howMany; i++) {
            	int startIndex = i * limit;
            	int endIndex = (howMany - i == 1) ? (twoMonthSize - 1) : (i + 1) * limit;
            	double endPrice = twoMonthSRs.get(endIndex).getNowPrice();
            	double startPrice = twoMonthSRs.get(startIndex).getNowPrice();
            	
            	//System.out.println(DateUtil.getDateByMilliseconds(twoMonthSRs.get(startIndex).getDate())
            	//		+ ": " + DateUtil.getDateByMilliseconds(twoMonthSRs.get(endIndex).getDate()));
            	double diff = endPrice - startPrice;
            	
            	upList.add(diff / startPrice);
            }
            //System.out.println(upList);
            
            //3-3, 统计个数
            int upSize = 0;
            for(double up: upList){
            	//价格幅度大于0的个数
            	if(up > 0)
            		upSize++;
            	//价格幅度在5%也算
            	if(up < 0 && up * 100 > -5.0)
            		upSize++;
            }
            //System.out.println(upSize);
            
            //3-4, 两个月处于上升通道
            flag = 3;
            if(upSize < howMany)
            	return flag;
            
            //4, 换手率明显大于之前
            flag = 4;
            if(twoMonthSize <= 20){
            	if(minSR.getHsl10() * 2 > twoMonthSRs.get(10).getHsl10())
                	return flag;
            }
            else if(twoMonthSize <= 30){
            	if(minSR.getHsl20() * 2 > twoMonthSRs.get(20).getHsl20())
                	return flag;
            }
        	else {
        		if(minSR.getHsl30() * 2 > twoMonthSRs.get(30).getHsl30())
        			return flag;
        	}
            
            //5, 两个月内至少3个大阳线(幅度在5%以上)
            flag = 5;
            int bigSunSize = 0;
            for(ScenarioResult sr: twoMonthSRs) {
            	Stock stock = getQueryStock(stockCode, sr.getDate());
            	if(stock.getChangeRate() > 5.0) {
            		bigSunSize++;
            	}
            }
            if(bigSunSize < 3)
            	return flag;
            
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ], minDate[ " +
					DateUtil.getDateByMilliseconds(minSR.getDate()) + "]");
			//saveMatchResult(stockCode, theDate, this.getClass().getSimpleName());
		} catch(Exception e) {
			logger.error("Error on calculate, code[ " + stockCode + " ], " + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}
	
	private List<ScenarioResult> getBeforeRangeSR(String stockCode, long theDateSecs, int days) {
        DateTime beforeDate = DateUtil.getDaysBefore(theDateSecs, days);
        long beforeSecs = DateUtil.getMilliseconds(beforeDate);
        List<ScenarioResult> srs = getRangeQuerySRs(stockCode, beforeSecs, theDateSecs);
        return srs;
	}
	
	private List<ScenarioResult> getAfterRangeSR(String stockCode, long theDateSecs, int days) {
        DateTime afterDate = DateUtil.getDaysAfter(theDateSecs, days);
        long afterSecs = DateUtil.getMilliseconds(afterDate);
        List<ScenarioResult> srs = getRangeQuerySRs(stockCode, theDateSecs, afterSecs);
        return srs;
	}
	
	private ScenarioResult findMinStockSR(List<ScenarioResult> srs) {
		double min = 1000;
		int flag = 0;
		for(int i = 0; i < srs.size(); i++) {
			if(min > srs.get(i).getNowPrice()){
				min = srs.get(i).getNowPrice();
				
				flag = i;
			}
		}
		return srs.get(flag);
		
	}

}