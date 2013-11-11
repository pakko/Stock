package com.ml.strategy;

import hirondelle.date4j.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.util.DateUtil;


public class StrategyD extends AbstractStrategy {

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
	private static final Logger logger = LoggerFactory.getLogger(StrategyD.class);

	public StrategyD(MongoDB mongodb) {
		super(mongodb);
	}
	
	public int calculate(String stockCode, String theDate) {
		int flag = 0;
		try{
			// 得到目前和10/20天前的ScenarioResult		
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			ScenarioResult theDateSR = getQuerySR(stockCode, theDateSecs);			
			
			DateTime beforeDate_20 = DateUtil.getIntervalWorkingDay(theDateSecs, 20, false);
			DateTime beforeDate_10 = DateUtil.getIntervalWorkingDay(theDateSecs, 10, false);
			
			DateTime beforeDate_2 = DateUtil.getIntervalWorkingDay(theDateSecs, 2, false);
			DateTime beforeDate_1 = DateUtil.getIntervalWorkingDay(theDateSecs, 1, false);

			long beforeDateSecs_10 = DateUtil.getMilliseconds(beforeDate_10);
			long beforeDateSecs_20 = DateUtil.getMilliseconds(beforeDate_20);
			
			long beforeDateSecs_1 = DateUtil.getMilliseconds(beforeDate_1);
			long beforeDateSecs_2 = DateUtil.getMilliseconds(beforeDate_2);
			
			ScenarioResult theDateSR_10 = getQuerySR(stockCode, beforeDateSecs_10);
			ScenarioResult theDateSR_20 = getQuerySR(stockCode, beforeDateSecs_20);
			
			ScenarioResult theDateSR_1 = getQuerySR(stockCode, beforeDateSecs_1);
			ScenarioResult theDateSR_2 = getQuerySR(stockCode, beforeDateSecs_2);
			
			if(theDateSR == null || theDateSR_10 == null || theDateSR_20 == null)
				return flag;
			//System.out.println(theDateSR + "_" + theDateSR_10 + "_" + theDateSR_10);
			
			//当前20天平均换手率大于前20天换手率2倍
			flag = 1;
			if( (theDateSR.getHsl20() / theDateSR_20.getHsl20()) < 2) {
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
			
			if( !(theDateSR_1.getMa5() >= theDateSR_1.getMa10()
					&& theDateSR_1.getMa10() >= theDateSR_1.getMa20() 
					&& theDateSR_1.getMa20() >= theDateSR_1.getMa30()) ) {
				return flag;
			}
			
			if( !(theDateSR_2.getMa5() >= theDateSR_2.getMa10()
					&& theDateSR_2.getMa10() >= theDateSR_2.getMa20() 
					&& theDateSR_2.getMa20() >= theDateSR_2.getMa30()) ) {
				return flag;
			}
			
			//60日涨幅不能大于50%
			flag = 3;
			if (theDateSR.getUp60() * 60 >= 30) {
				return flag;
			}
			
			//5日，10日，20日，30日四个均价相差10%以内来代替(基数为4个均价的均值)
			/*flag = 4;
			double avgOfAP = (theDateSR.getMa5() + theDateSR.getMa10() +  theDateSR.getMa20() + theDateSR.getMa30()) / 4;
			double fiveDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa5()) / avgOfAP;
			double tenDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa10()) / avgOfAP;
			double twentyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa20()) / avgOfAP;
			double thirtyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa30()) / avgOfAP;
			if( !(fiveDiffOfAP < 0.1 && tenDiffOfAP < 0.1 && twentyDiffOfAP < 0.1 && thirtyDiffOfAP < 0.1)) {
				return flag;
			}*/
			
			//5日均价与10日均价相差在2%之内，当前价格不高于5日均价3%
			/*flag = 5;
			double nowPrice = theDateSR.getNowPrice();
			if ((theDateSR.getMa5() - theDateSR.getMa10()) / nowPrice > 0.02) {
				return flag;
			}*/
			flag = 6;
			double nowPrice = theDateSR.getNowPrice();
			/*if ((nowPrice - theDateSR.getMa5()) / nowPrice > 0.03 &&
					(nowPrice - theDateSR.getMa5()) / nowPrice < -0.03) {
				return flag;
			}*/
			
			if (nowPrice < theDateSR.getMa5()) {
				return flag;
			}
			
			if (((nowPrice - theDateSR.getMa5()) / nowPrice) > 0.05) {
				return flag;
			}
			
			//目前10平均换手率大于前10天平均换手率
			flag = 7;
			if (!(theDateSR.getHsl10() > theDateSR_10.getHsl10())) {
				return flag;
			}
			
			//30日涨幅不能大于30%
			flag = 8;
			if (theDateSR.getUp30() * 30 > 30) {
				return flag;
			}
			
			//连续3日收盘价在5日均线之上
			/*flag = 9;
			if (!(theDateSR.getNowPrice() > theDateSR.getMa5() &&
					theDateSR_1.getNowPrice() > theDateSR_1.getMa5() &&
					theDateSR_2.getNowPrice() > theDateSR_2.getMa5())) {
				return flag;
			}*/
			
			//比较斜率
			flag = 9;			
			double xl1 = theDateSR.getMa5() - theDateSR_1.getMa5();
			double xl2 = theDateSR_1.getMa5() - theDateSR_2.getMa5();
			
			if (!(xl1 > xl2)) {
				return flag;
			}
			
			flag = 10;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			saveMatchResult(stockCode, theDate);
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
			e.printStackTrace();
		}
		return flag;
	}

}