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


public class StrategyF extends AbstractStrategy {

	/* 
     	1. 找到2年内股票的最低点
     	2. 股票的最低点距当前不超过4个月,最好是2-3个月
     	3. 最低点到1年前的股价是渐次降低（每个月的斜率是下降的，占比60%，价格幅度在5%时忽略不计时占比80%）
		4. 最低点到现在股价涨幅不超过80%
		5. 趋势反转后,换手率如果按月计算,是属于持续放大,或者后一月至少不会小于前一个月, 而且对比反转前至少半年的换手率都明显大
		6. 反转后,后一个月的均价不小于前一个月的均价
		7. 底部大阳线居多
     */
	private static final Logger logger = LoggerFactory.getLogger(StrategyF.class);

	public StrategyF(MongoDB mongodb, Boolean isReal) {
		super(mongodb, isReal);
	}
	
	private int DAYS_OF_YEAR = 365;
	private int MONTH_OF_YEAR = 12;
	
	public int calculate(String stockCode, String theDate) {
		int flag = 0;
        try{
        	int howManyYear = 2;
        	
            long theDateSecs = DateUtil.getMilliseconds(theDate);
            //1-1, 得到2年的股价
            flag = 1;
            List<ScenarioResult> twoYearSRs = getBeforeRangeSR(stockCode, theDateSecs, DAYS_OF_YEAR * howManyYear);
            if(twoYearSRs.size() < DAYS_OF_YEAR)
            	return flag;
            
            //1-2, 得到最低股价
            ScenarioResult minSR = findMinStockSR(twoYearSRs);
            		
            //2, 股票的最低点距当前不超过4个月,最好是2-3个月
            flag = 2;
            DateTime beforeDate = DateUtil.getDaysBefore(theDateSecs, 30 * 6);
            long beforeSecs = DateUtil.getMilliseconds(beforeDate);
            //System.out.println(DateUtil.getDateByMilliseconds(minSR.getDate()) + ": " + beforeDate);
            if(minSR.getDate() < beforeSecs)
            	return flag; 
            
            //3-1, 得到minSR前2年内每个月的涨跌幅
            List<ScenarioResult> twoYearToMinSRs = getBeforeRangeSR(stockCode, minSR.getDate(), DAYS_OF_YEAR * howManyYear);
            int twoYearToMinSize = twoYearToMinSRs.size();
            int limit = twoYearToMinSize / (MONTH_OF_YEAR * howManyYear);

            List<Double> monthUpList = new ArrayList<Double>(MONTH_OF_YEAR * howManyYear);
            for(int i = 0; i < MONTH_OF_YEAR * howManyYear; i++) {
            	int startIndex = i * limit;
            	int endIndex = (MONTH_OF_YEAR * howManyYear - i == 1) ? (twoYearToMinSize - 1) : (i + 1) * limit;
            	double endPrice = twoYearToMinSRs.get(endIndex).getNowPrice();
            	double startPrice = twoYearToMinSRs.get(startIndex).getNowPrice();
            	double diff = endPrice - startPrice;
            	
            	//System.out.println(DateUtil.getDateByMilliseconds(oneYearSRs.get(startIndex).getDate())
            	//		+ ": " + DateUtil.getDateByMilliseconds(oneYearSRs.get(endIndex).getDate()));
            	monthUpList.add(diff / startPrice);
            }
            //System.out.println(monthUpList);
            
            //3-2, 统计个数
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
            
            //3-3, 占比40%以上时返回
            flag = 3;
            if(downSize / DAYS_OF_YEAR > 0.4)
            	return flag;
            
            //3-4, 价格幅度在5%时忽略不计时占比80%
            flag = 4;
            if(strickDownSize / DAYS_OF_YEAR > 0.2)
            	return flag;
            
            int howManyMonth = 2;
            int stock_days_of_month = 20;
            
            //5-1, 得到开始放量的SR
            List<ScenarioResult> minToCurrentSRs = getRangeQuerySRs(stockCode, minSR.getDate(), theDateSecs);
            ScenarioResult flySR = findFlyStockSR(stockCode, minToCurrentSRs);
            flag = 5;
            if(flySR == null)
            	return flag;
            List<ScenarioResult> flyToCurrentSRs = getRangeQuerySRs(stockCode, flySR.getDate(), theDateSecs);

            //System.out.println(minSR);
            //System.out.println(DateUtil.getDateByMilliseconds(flySR.getDate()));
            
            int flyToCurrentSize = flyToCurrentSRs.size();
            flag = 6;
            if(flyToCurrentSize < stock_days_of_month)
            	return flag;
            
            //System.out.println(twoMonthSize);
            howManyMonth = (int) Math.round(flyToCurrentSize * 1.0 / stock_days_of_month);
            //howManyMonth = 2;
            
            double totalHsl = 0.0;
            for(int i = 0; i < howManyMonth; i++) {
            	int startIndex = i * stock_days_of_month;
            	int endIndex = (howManyMonth - i == 1) ? (flyToCurrentSize - 1) : (i + 1) * stock_days_of_month;
            	ScenarioResult endSR = flyToCurrentSRs.get(endIndex);
            	ScenarioResult startSR = flyToCurrentSRs.get(startIndex);
            	
            	//5-2, 趋势反转后,换手率如果按月计算,是属于持续放大,或者后一月至少不会小于前一个月
            	flag = 7;
            	double rate = (startSR.getHsl20() - endSR.getHsl20()) / startSR.getHsl20();
            	if(startSR.getHsl20() > endSR.getHsl20()){
            		if(rate > 0.2)
            			return flag;
                }
            	//6, 反转后,后一个月的均价不小于前一个月的均价
        		flag = 8;
                if(startSR.getMa20() > endSR.getMa20()) {
                	System.out.println("flag: " + flag + " - " + stockCode + ", date[ " + theDate + " ], flyDate[ " +
        					DateUtil.getDateByMilliseconds(flySR.getDate()) + "]");
                    	return flag;
                }
                flag = 9;
                if(startSR.getNowPrice() > endSR.getNowPrice()) {
                	return flag;
                }
            	totalHsl += startSR.getHsl20() + endSR.getHsl20();
            	//System.out.println(DateUtil.getDateByMilliseconds(oneYearSRs.get(startIndex).getDate())
            	//		+ ": " + DateUtil.getDateByMilliseconds(oneYearSRs.get(endIndex).getDate()));
            }
            
            //7, 两个月内至少3个大阳线(幅度在5%以上)
            int bigSunSize = 0;
            for(ScenarioResult sr: minToCurrentSRs) {
            	Stock stock = getQueryStock(stockCode, sr.getDate());
            	if(stock == null)
            		continue;
            	if(stock.getChangeRate() > 5.0) {
            		bigSunSize++;
            	}
            }
            flag = 10;
            if(bigSunSize < 3)
            	return flag;
            
            //5-3, 目前的换手率总量比反转前至少半年的换手率都明显大
            flag = 11;
            if(flySR.getHsl120() * 120 > totalHsl * 20){
            	System.out.println("flag: " + flag + " - " + stockCode + ", date[ " + theDate + " ], flyDate[ " +
					DateUtil.getDateByMilliseconds(flySR.getDate()) + "]");
            	return flag;
            }
            
            //4, 最低点到现在股价涨幅不超过80%
    		ScenarioResult theDateSR = flyToCurrentSRs.get(flyToCurrentSize - 1);
            double upRate = (theDateSR.getNowPrice() - flySR.getNowPrice()) / flySR.getNowPrice();
            flag = 12;
            if(upRate > 0.8){
            	System.out.println("flag: " + flag + " - " + stockCode + ", date[ " + theDate + " ], flyDate[ " +
    					DateUtil.getDateByMilliseconds(flySR.getDate()) + "]");
                	return flag;
            }
            
            flag = 13;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ], flyDate[ " +
					DateUtil.getDateByMilliseconds(flySR.getDate()) + "]");
			saveMatchResult(stockCode, flySR.getDate(), theDateSecs, this.getClass().getSimpleName());
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
	
	private ScenarioResult findFlyStockSR(String stockCode, List<ScenarioResult> srs) {
		double min = srs.get(0).getHsl20() * 2;
		List<Integer> list = new ArrayList<Integer>();
		for(int i = 0; i < srs.size(); i++) {
			Stock stock = getQueryStock(stockCode, srs.get(i).getDate());
			if(stock == null)
				continue;
			//System.out.println(stock + ": " + min);
			//System.out.println(DateUtil.getDateByMilliseconds(stock.getDate()));
			if(stock.getTurnOverRate() <= min) {
				min = srs.get(i).getHsl20() * 2;
			}
			else {
				list.add(i);
			}
		}
		int[] arr = new int[list.size()];;
		int i = 0;
		for(int a: list){
			arr[i++] = a;
		}
		int index = find3Seq(arr);
		if(index != list.size())
			return srs.get(arr[index]);
		return null;
	}
	
	public int find3Seq(int[] arr){
        int index = 0;
        while(index < arr.length - 3){
            if(is3Seq(arr, index)){
                break;
            }
            index++;
        }
        return index;
    }
     
    private boolean is3Seq(int[] arr, int index){
        return arr[index] + 1 == arr[index + 1] 
        	&& arr[index + 1] + 1 == arr[index + 2];
        	//&& arr[index + 2] + 1 == arr[index + 3]; 
    }

}