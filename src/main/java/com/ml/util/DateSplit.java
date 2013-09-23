package com.ml.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DateSplit {
	private final static int SPLIT_DAYS = 100;

	public static List<String[]> split(String startDateStr, String endDateStr)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date startDate = sdf.parse(startDateStr);
		Date endDate = sdf.parse(endDateStr);
		
		if (!startDate.before(endDate))
			throw new Exception("开始时间应该在结束时间之后");
		
		long days = getDaysBetween(startDate, endDate);
		System.out.println(days);
		
		int step = (int) Math.ceil(days / (SPLIT_DAYS * 1.0));
		System.out.println(step);

		Date newStartDate = (Date) startDate.clone();
		Date newEndDate = (Date) endDate.clone();
		
		List<String[]> dataList = new ArrayList<String[]>(step);
		for(int i = 0; i < step; i++) {
			String[] dates = new String[2];
			newStartDate = getBeforeDate(startDate, newEndDate);
			dates[0] = sdf.format(newStartDate);
			dates[1] = sdf.format(newEndDate);
			dataList.add(dates);
			newEndDate = getNextStartDate(newStartDate);
		}
		return dataList;
	}
	
	private static Date getBeforeDate(Date startDate, Date endDate) {
		Calendar now = Calendar.getInstance();
		now.setTime(endDate);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - SPLIT_DAYS);
		if(now.getTime().getTime() < startDate.getTime()) {
			return startDate;
		}
		return now.getTime();
	}
	
	private static Date getNextStartDate(Date date) {
		Calendar now = Calendar.getInstance();
		now.setTime(date);
		now.set(Calendar.DATE, now.get(Calendar.DATE) - 1);
		return now.getTime();
	}
	
	public static void main(String[] args) throws ParseException {
		try {
			List<String[]> lists = split("2012-11-01", "2012-12-01");
			if (!lists.isEmpty()) {
				for (String[] dates : lists) {
					System.out.println("start date: " + dates[0] + ", end date: " + dates[1]);
				}

			}
		} catch (Exception e) {
			System.out.print(e);
		}

	}
	
	private static Long getDaysBetween(Date startDate, Date endDate) {
		Calendar fromCalendar = Calendar.getInstance();
		fromCalendar.setTime(startDate);
		fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
		fromCalendar.set(Calendar.MINUTE, 0);
		fromCalendar.set(Calendar.SECOND, 0);
		fromCalendar.set(Calendar.MILLISECOND, 0);

		Calendar toCalendar = Calendar.getInstance();
		toCalendar.setTime(endDate);
		toCalendar.set(Calendar.HOUR_OF_DAY, 0);
		toCalendar.set(Calendar.MINUTE, 0);
		toCalendar.set(Calendar.SECOND, 0);
		toCalendar.set(Calendar.MILLISECOND, 0);

		return (toCalendar.getTime().getTime() - fromCalendar.getTime().getTime()) / (1000 * 60 * 60 * 24);
	}

}
