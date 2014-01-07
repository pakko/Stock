package com.ml.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import hirondelle.date4j.DateTime;


public class DateUtil {
	private static Map<Integer, String[]> statutoryHoliday = new HashMap<Integer, String[]>() {
		private static final long serialVersionUID = -2115238609924948036L;
		{
			put(2012, new String[] {"01-02", "01-03", "01-23", "01-24", "01-25", "01-26", "01-27", "04-02", "04-03", "04-04", "04-30", 
					"05-01", "05-02", "06-22", "10-01", "10-02", "10-03", "10-04", "10-05"});
			put(2013, new String[] {"01-01", "01-02", "01-03", "02-11", "02-12", "02-13", "02-14", "02-15", "04-04", "04-05", "04-29", "04-30", 
					"05-01", "06-10", "06-11", "06-12", "09-19", "09-20", "10-01", "10-02", "10-03", "10-04", "10-07"});
			put(2014, new String[] {"01-01", "01-31", "02-01", "02-02", "02-03", "02-04", "02-05", "02-06", "04-07", "05-01", "05-02", "05-03", 
					"06-02", "09-08", "10-01", "10-02", "10-03", "10-04", "10-05", "10-06", "10-07"});
		}
	};
	private static final String FORMAT_PATTERN = "YYYY-MM-DD";
	private static TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

	public static void main(String[] args) {
		String beginDate = "2013-01-01";
		String endDate = "2013-10-27";
		
		long days = getDaysBetween(new DateTime(beginDate), new DateTime(endDate));
		System.out.println("days: " + days);
		long theDateSecs = getMilliseconds(endDate);
		DateTime beforeDate_1 = getIntervalWorkingDay(theDateSecs, 1, true);
		System.out.println("before: " + beforeDate_1);


	}
	
	public static DateTime getDateByMilliseconds(long milliseconds) {
		return DateTime.forInstant(milliseconds, timeZone);
	}
	
	public static long getMilliseconds(String dateStr) {
		DateTime date = new DateTime(dateStr);
		return getMilliseconds(date);
	}
	
	public static long getMilliseconds(DateTime date) {
		return date.getMilliseconds(timeZone);
	}
	
	public static <T> List<List<T>> splitList(List<T> list, int splitSize) {
		int size = list.size();
		int step = (int) Math.ceil(size / (splitSize * 1.0));
		//System.out.println("step: " + step + ", dateList size: " + size);
		//System.out.println("dateList: " + list);

		List<List<T>> dataList = new ArrayList<List<T>>(step);
		for(int j = 0; j < step; j++) {
			int fromIndex = j * splitSize;
			int toIndex = (j + 1) * splitSize;
			toIndex = toIndex < size ? toIndex : size;
			
			//System.out.println(fromIndex + ":" + toIndex);
			List<T> subList = list.subList(fromIndex, toIndex);
			dataList.add(subList);
		}
		return dataList;
	}
	
	public static List<String> getWorkingDays(String startDateStr, String endDateStr) {
		DateTime startDate = new DateTime(startDateStr);
		DateTime endDate = new DateTime(endDateStr);
		
		//get holidays
		List<String> holidays = getHolidaysAndWeekendsByRange(startDate, endDate);
		//System.out.println("holidays: " + holidays.size());

		//calculate days
		long days = getDaysBetween(startDate, endDate);
		//System.out.println("days: " + days);
		
		//get working days
		List<String> dateList = new ArrayList<String>();
		for(int i = 0; i < days; i++) {
			String date = startDate.format(FORMAT_PATTERN);
			boolean isHoliday = validate(date, holidays);
			if(!isHoliday){
				dateList.add(date);
			}
			startDate = startDate.plusDays(1);
		}
		return dateList;
	}
	
	public static DateTime getIntervalWorkingDay(long startDateSecs, int interval, boolean isNext) {
		int times = interval > 5 ? interval * 5 : interval * 10;	//expand
		DateTime startDate = getDateByMilliseconds(startDateSecs);
		DateTime endDate = isNext ? startDate.plusDays(times) : startDate.minusDays(times);
		
		List<String> days;
		List<String> holidays;
		String starDateStr = startDate.format(FORMAT_PATTERN);
		String endDateStr = endDate.format(FORMAT_PATTERN);
		if(getMilliseconds(endDate) < getMilliseconds(startDate)) {
			days = getWorkingDays(endDateStr, starDateStr);
			holidays = getHolidaysAndWeekendsByRange(endDate, startDate);
		}
		else {
			days = getWorkingDays(starDateStr, endDateStr);
			holidays = getHolidaysAndWeekendsByRange(startDate, endDate);
		}
		int index = isNext ? interval : (days.size() - interval - 1);
		boolean isHoliday = validate(starDateStr, holidays);
		if(isHoliday)
			index = isNext ? index - 1 : index + 1;
		return new DateTime(days.get(index));
	}
	
	public static DateTime getBeforeWorkingDay(long startDateSecs, int interval) {
		int times = interval > 5 ? interval * 5 : interval * 10;	//expand
		DateTime startDate = getDateByMilliseconds(startDateSecs);
		DateTime endDate = startDate.minusDays(times);
		
		String starDateStr = startDate.format(FORMAT_PATTERN);
		String endDateStr = endDate.format(FORMAT_PATTERN);
		List<String> days = getWorkingDays(endDateStr, starDateStr);
		List<String> holidays = getHolidaysAndWeekendsByRange(endDate, startDate);
		
		int index = days.size() - interval - 1;
		boolean isHoliday = validate(starDateStr, holidays);
		if(isHoliday)
			index += 1;
		return new DateTime(days.get(index));
	}
	
	private static long getDaysBetween(DateTime startDate, DateTime endDate) {
		long diff = getMilliseconds(endDate) - getMilliseconds(startDate);
		return diff / (1000*3600*24) + 1;
	}

	private static List<String> getHolidaysAndWeekendsByRange(DateTime startDate, DateTime endDate) {
		int startYear = startDate.getYear();
		int endYear = endDate.getYear();

		List<String> total = new ArrayList<String>();
		while(startYear <= endYear) {
			List<String> days = getHolidaysAndWeekendsByYear(startYear);
			total.addAll(days);
			startYear++;
		}
		return total;
	}
	
	private static List<String> getHolidaysAndWeekendsByYear(int year) {
		List<String> weekendDays = getWeekends(year);
		addStatutoryHolidays(year, weekendDays);
		return weekendDays;
	}

	private static boolean validate(String dateStr, List<String> holidays) {
		if(holidays.contains(dateStr))
			return true;
		return false;
	}

	private static void addStatutoryHolidays(int year, List<String> weekenDays) {
		String[] statutoryHolidays = statutoryHoliday.get(year);
		if(statutoryHolidays == null || statutoryHolidays.length <= 0)
			return;
		for(String holiday: statutoryHolidays){
			weekenDays.add(year + "-" + holiday);
		}
	}
	
	private static List<String> getWeekends(int year){
		List<String> list = new ArrayList<String>();
		
		DateTime date = DateTime.forDateOnly(year, 12, 31);
		int days = date.getDayOfYear();
		
		for(int day = 1; day <= days; day++){
			int weekDay = date.getWeekDay();
			
			if(weekDay == Calendar.SATURDAY || weekDay == Calendar.SUNDAY){
				list.add(date.format(FORMAT_PATTERN));
			}
			date = date.minusDays(1);
		}
		return list;
	}

}
