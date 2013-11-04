package com.ml.strategy;

import hirondelle.date4j.DateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ml.db.MongoDB;
import com.ml.model.ScenarioResult;
import com.ml.util.DateUtil;


public class StrategyB extends AbstractStrategy {

	/*
	 * 1. 计算120天内的平均换手率
	 * 2. 计算前120天内的平均换手率
	 * 3. 120天内平均换手率大于前120天换手率(幅度2倍以上)
	 * 3. 计算5日，10日，20日，30日，60日均价
	 * 4. 5日和10日均价大于等于20日均价，20日均价大于等于30日均价，30日均价大于等于60日均价
	 * 5. 120日内涨幅小于等于50%
	 * 6. 5日，10日，20日，30日均价相差在10%以内
	 * 7. 5日，10日均价相差在2%以内
	 * 8. 计算近几天5日均线斜率是否有增大趋势
	 * 
	 */
	private static final Logger logger = LoggerFactory.getLogger(StrategyB.class);

	public StrategyB(MongoDB mongodb) {
		super(mongodb);
	}
	
    public int calculate(String stockCode, String theDate) {
    	int flag = 0;
		try{
			// 得到目前和120天前的ScenarioResult		
			long theDateSecs = DateUtil.getMilliseconds(theDate);
			
			DateTime beforeDate = DateUtil.getBeforeWorkingDay(theDateSecs, 120);
			long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);

			DateTime beforeDate_1 = DateUtil.getBeforeWorkingDay(theDateSecs, 1);
			long beforeDateSecs_1 = DateUtil.getMilliseconds(beforeDate_1);
	
			ScenarioResult theDateSR = getQuerySR(stockCode, theDateSecs);
			ScenarioResult beforeDateSR = getQueryNearSR(stockCode, beforeDateSecs);
			ScenarioResult theDateSR_1 = getQuerySR(stockCode, beforeDateSecs_1);	

			if(theDateSR == null || beforeDateSR == null || theDateSR_1 == null)
				return flag;
			//System.out.println(theDateSR + "_" + beforeDateSR + "_" + theDateSR_1);
			
			//当前120天平均换手率大于前120天换手率2倍以上
			flag = 1;
			if( (theDateSR.getHsl120() / beforeDateSR.getHsl120()) < 2 ) {
				return flag;
			}
			
			//5日，10日均价大于等于20日均价，20日均价大于 等于30日均价，30日均价大于等于60日均价
			//保证股票运行在上升通道中 
			flag = 2;
			if( !(theDateSR.getMa5() >= theDateSR.getMa10()
					&& theDateSR.getMa10() >= theDateSR.getMa20() 
					&& theDateSR.getMa20() >= theDateSR.getMa30()
					&& theDateSR.getMa30() >= theDateSR.getMa60()) ) {
				return flag;
			}
			
			//涨幅不能大于50%
			flag = 3;
			if (theDateSR.getUp120() * 120 >= 50) {
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
			
			//比较斜率
			flag = 8;
			DateTime beforeDate_2 = DateUtil.getBeforeWorkingDay(theDateSecs, 2);
			long beforeDateSecs_2 = DateUtil.getMilliseconds(beforeDate_2);
			ScenarioResult theDateSR_2 = getQuerySR(stockCode, beforeDateSecs_2);
			
			DateTime beforeDate_3 = DateUtil.getBeforeWorkingDay(theDateSecs, 3);
			long beforeDateSecs_3 = DateUtil.getMilliseconds(beforeDate_3);
			ScenarioResult theDateSR_3 = getQuerySR(stockCode, beforeDateSecs_3);
			
			DateTime beforeDate_4 = DateUtil.getBeforeWorkingDay(theDateSecs, 4);
			long beforeDateSecs_4 = DateUtil.getMilliseconds(beforeDate_4);
			ScenarioResult theDateSR_4 = getQuerySR(stockCode, beforeDateSecs_4);
			
			if(theDateSR_2 == null || theDateSR_3 == null || theDateSR_4 == null)
				return flag;
			
			flag = 9;
			double xl1 = theDateSR.getMa5() - theDateSR_1.getMa5();
			double xl2 = theDateSR_1.getMa5() - theDateSR_2.getMa5();
			double xl3 = theDateSR_2.getMa5() - theDateSR_3.getMa5();
			double xl4 = theDateSR_3.getMa5() - theDateSR_4.getMa5();
			
			if (!(xl1 >0 && xl2 >0 && xl1 > xl2)) {
				return flag;
			}
			
			flag = 10;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			saveMatchResult(stockCode, theDate);
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
		}
		return flag;
	}


}