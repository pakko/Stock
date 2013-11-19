package com.ml.strategy;

import hirondelle.date4j.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.util.DateUtil;


public class StrategyC extends AbstractStrategy {

	/*
	 * 1. 计算120天内的平均换手率
	 * 2. 计算前120天内的平均换手率
	 * 3. 120天内平均换手率大于前120天换手率
	 * 3. 计算5日，10日，20日，30日
	 * 4. 5日均价大于10日均价，10日均价大于等于20日均价，20日均价大于等于30日均价
	 * 5. 120日内涨幅小于等于30%
	 * 6. 5日，10日，20日，30日均价相差在10%以内
	 * 7. 5日，10日均价相差在2%以内
	 * 8. 连续三天收盘价在5日均线之上
	 * 9。 20天上涨幅度小于20%
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(StrategyC.class);

	public StrategyC(MongoDB mongodb, Boolean isReal) {
		super(mongodb, isReal);
	}
	
	public int calculate(String stockCode, String theDate) {
		int flag = 0;
		try{
			// 得到目前和120天前的ScenarioResult		
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDateSecs, 120, false);
			DateTime beforeDate_1 = DateUtil.getIntervalWorkingDay(theDateSecs, 1, false);
			DateTime beforeDate_2 = DateUtil.getIntervalWorkingDay(theDateSecs, 2, false);
			long beforeDateSecs_1 = DateUtil.getMilliseconds(beforeDate_1);
			long beforeDateSecs_2 = DateUtil.getMilliseconds(beforeDate_2);
			ScenarioResult theDateSR_1 = getQuerySR(stockCode, beforeDateSecs_1);
			ScenarioResult theDateSR_2 = getQuerySR(stockCode, beforeDateSecs_2);
			
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);
	
			ScenarioResult theDateSR = getQuerySR(stockCode, theDateSecs);
			ScenarioResult beforeDateSR = getQueryNearSR(stockCode, beforeDateSecs);

			if(theDateSR == null || beforeDateSR == null || theDateSR_1 == null)
				return flag;

			//当前120天平均换手率大于前120天换手率
			flag = 1;
			if( theDateSR.getHsl120() < beforeDateSR.getHsl120()) {
				return flag;
			}
			
			//5日，10日均价大于等于20日均价，20日均价大于 等于30日均价
			//保证股票运行在上升通道中 
			flag = 2;
			if( !(theDateSR.getMa5() >= theDateSR.getMa10()
					&& theDateSR.getMa10() >= theDateSR.getMa20() 
					&& theDateSR.getMa20() >= theDateSR.getMa30()) ) {
				return flag;
			}
			
			//120日涨幅不能大于30%
			flag = 3;
			if (theDateSR.getUp120() * 120 >= 30) {
				return flag;
			}
			
			//5日，10日，20日，30日四个均价相差10%以内来代替(基数为4个均价的均值)
			flag = 4;
			double avgOfAP = (theDateSR.getMa5() + theDateSR.getMa10() +  theDateSR.getMa20() + theDateSR.getMa30()) / 4;
			double fiveDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa5()) / avgOfAP;
			double tenDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa10()) / avgOfAP;
			double twentyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa20()) / avgOfAP;
			double thirtyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa30()) / avgOfAP;
			if( !(fiveDiffOfAP < 0.1 && tenDiffOfAP < 0.1 && twentyDiffOfAP < 0.1 && thirtyDiffOfAP < 0.1)) {
				return flag;
			}
			
			//5日均价与10日均价相差在2%之内，当前价格不高于5日均价3%
			flag = 5;
			double nowPrice = theDateSR.getNowPrice();
			if ((theDateSR.getMa5() - theDateSR.getMa10()) / nowPrice > 0.02) {
				return flag;
			}
			flag = 6;
			if ((nowPrice - theDateSR.getMa5()) / nowPrice > 0.03) {
				return flag;
			}
			
			//目前5日均价大于前一天5日均价
			flag = 7;
			if (!(theDateSR.getMa5() > theDateSR_1.getMa5() &&
					theDateSR.getMa10() > theDateSR_1.getMa10())) {
				return flag;
			}
			
			//30日涨幅小于10%
			flag = 8;
			if (theDateSR.getUp30() * 30 > 10) {
				return flag;
			}
			
			//连续3日收盘价在5日均线之上
			flag = 9;
			if (!(theDateSR.getNowPrice() > theDateSR.getMa5() &&
					theDateSR_1.getNowPrice() > theDateSR_1.getMa5() &&
					theDateSR_2.getNowPrice() > theDateSR_2.getMa5())) {
				return flag;
			}
			
			flag = 10;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			saveMatchResult(stockCode, theDate, this.getClass().getSimpleName());
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

}