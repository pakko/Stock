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
			// �õ�Ŀǰ��120��ǰ��ScenarioResult		
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
			//System.out.println(theDateSR + "_" + beforeDateSR + "_" + theDateSR_1);
			
			//��ǰ120��ƽ�����ʴ���ǰ120�컻����
			flag = 1;
			if( theDateSR.getHsl120() < beforeDateSR.getHsl120()) {
				return flag;
			}
			
			//5�գ�10�վ�۴��ڵ���20�վ�ۣ�20�վ�۴��� ����30�վ��
			//��֤��Ʊ����������ͨ���� 
			flag = 2;
			if( !(theDateSR.getMa5() >= theDateSR.getMa10()
					&& theDateSR.getMa10() >= theDateSR.getMa20() 
					&& theDateSR.getMa20() >= theDateSR.getMa30()) ) {
				return flag;
			}
			
			//120���Ƿ��ܴ���30%
			flag = 3;
			if (theDateSR.getUp120() >= 30) {
				return flag;
			}
			
			//5�գ�10�գ�20�գ�30���ĸ�������10%����������(����Ϊ4����۵ľ�ֵ)
			flag = 4;
			double avgOfAP = (theDateSR.getMa5() + theDateSR.getMa10() +  theDateSR.getMa20() + theDateSR.getMa30()) / 4;
			double fiveDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa5()) / avgOfAP;
			double tenDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa10()) / avgOfAP;
			double twentyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa20()) / avgOfAP;
			double thirtyDiffOfAP = Math.abs(avgOfAP - theDateSR.getMa30()) / avgOfAP;
			if( !(fiveDiffOfAP < 0.1 && tenDiffOfAP < 0.1 && twentyDiffOfAP < 0.1 && thirtyDiffOfAP < 0.1)) {
				return flag;
			}
			
			//5�վ����10�վ�������2%֮�ڣ���ǰ�۸񲻸���5�վ��3%
			flag = 5;
			double nowPrice = theDateSR.getNowPrice();
			if ((theDateSR.getMa5() - theDateSR.getMa10()) / nowPrice > 0.02) {
				return flag;
			}
			flag = 6;
			if ((nowPrice - theDateSR.getMa5()) / nowPrice > 0.03) {
				return flag;
			}
			
			//Ŀǰ5�վ�۴���ǰһ��5�վ��
			flag = 7;
			if (!(theDateSR.getMa5() > theDateSR_1.getMa5() &&
					theDateSR.getMa10() > theDateSR_1.getMa10())) {
				return flag;
			}
			
			//30���Ƿ�С��10%
			flag = 8;
			if (theDateSR.getUp30() > 10) {
				return flag;
			}
			
			//����3�����̼���5�վ���֮��
			flag = 9;
			if (!(theDateSR.getNowPrice() > theDateSR.getMa5() &&
					theDateSR_1.getNowPrice() > theDateSR_1.getMa5() &&
					theDateSR_2.getNowPrice() > theDateSR_2.getMa5())) {
				return flag;
			}
			
			System.out.println(theDateSR.getCode() + "-" + theDateSR.getMa5() + "-" + theDateSR.getNowPrice());
			System.out.println(theDateSR_1.getCode() + "-" + theDateSR_1.getMa5() + "-" + theDateSR_1.getNowPrice());
			System.out.println(theDateSR_2.getCode() + "-" + theDateSR_2.getMa5() + "-" + theDateSR_2.getNowPrice());
			
			//�Ƚ�б��
			/*flag = 8;
			DateTime beforeDate_2 = DateUtil.getIntervalWorkingDay(beforeDateSecs_1, 1, false);
			long beforeDateSecs_2 = DateUtil.getMilliseconds(beforeDate_2);
			ScenarioResult theDateSR_2 = getQuerySR(stockCode, beforeDateSecs_2);
			
			DateTime beforeDate_3 = DateUtil.getIntervalWorkingDay(beforeDateSecs_2, 1, false);
			long beforeDateSecs_3 = DateUtil.getMilliseconds(beforeDate_3);
			ScenarioResult theDateSR_3 = getQuerySR(stockCode, beforeDateSecs_3);
			
			DateTime beforeDate_4 = DateUtil.getIntervalWorkingDay(beforeDateSecs_3, 1, false);
			long beforeDateSecs_4 = DateUtil.getMilliseconds(beforeDate_4);
			ScenarioResult theDateSR_4 = getQuerySR(stockCode, beforeDateSecs_4);
			
			double xl1 = theDateSR.getMa5() - theDateSR_1.getMa5();
			double xl2 = theDateSR_1.getMa5() - theDateSR_2.getMa5();
			double xl3 = theDateSR_2.getMa5() - theDateSR_3.getMa5();
			double xl4 = theDateSR_3.getMa5() - theDateSR_4.getMa5();
			
			if (!(xl1 >0 && xl2 >0 && xl1 > xl2)) {
				return flag;
			}*/
			
			flag = 10;
			logger.info("Match stock: code[ " + stockCode + " ], date[ " + theDate + " ]");
			saveMatchResult(stockCode, theDate);
		} catch(Exception e) {
			logger.error("Error on calculate, " + e.getMessage());
		}
		return flag;
	}


}