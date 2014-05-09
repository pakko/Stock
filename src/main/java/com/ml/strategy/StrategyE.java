package com.ml.strategy;

import java.util.Calendar;
import java.util.Date;
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
	
	public int calculate(String stockCode, String theDate) {
		int flag = 0;
        try{
            long theDateSecs = DateUtil.getMilliseconds(theDate);
            DateTime before_2year_date = DateUtil.getDaysBefore(theDateSecs, 365*2);
            long beforeDateSecs_2year = DateUtil.getMilliseconds(before_2year_date);
            
            //得到2年的股价
            List<ScenarioResult> srs = getRangeQuerySRs(stockCode, beforeDateSecs_2year, theDateSecs);
            //得到最低股价
            ScenarioResult minSR = findMinStockSR(srs);
            
            DateTime before_1year_date = DateUtil.getDaysBefore(minSR.getDate(), 365);
            
            
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			//saveMatchResult(stockCode, theDate, this.getClass().getSimpleName());
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
			e.printStackTrace();
		}
		return flag;
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