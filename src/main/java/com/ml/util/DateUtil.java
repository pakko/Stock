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
			put(2013, new String[] {"01-01", "01-02", "01-03", "02-11", "02-12", "02-13", "02-14", "02-15", "04-04", "04-05", "04-29", "04-30", 
					"05-01", "06-10", "06-11", "06-12", "09-19", "09-20", "10-01", "10-02", "10-03", "10-04", "10-07"});
		}
	};
	private static final String FORMAT_PATTERN = "YYYY-MM-DD";
	private static TimeZone timeZone = TimeZone.getTimeZone("GMT+8");

	public static void main(String[] args) {
		String beginDate = "2012-01-01";
		String endDate = "2013-10-20";
		List<String[]> splitDates = getSplitDates(beginDate, endDate, Constants.SplitDays);
		for(String[] dates: splitDates) {
			System.out.println(dates[0] + ":" + dates[1]);
		}
		//getWorkingDays(beginDate, endDate);
		DateTime d = getIntervalWorkingDay(new DateTime("2013-10-20"), 5, true);
		System.out.println(d);

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
	
	public static List<String> truncateDateList(List<String> dateList, String dateStr, int interval) {
		DateTime date = DateUtil.getIntervalWorkingDay(new DateTime(dateStr), interval, true);
		String dateFormat = date.format(FORMAT_PATTERN);
		return dateList.subList(dateList.indexOf(dateFormat), dateList.size() - 1);
	}
	
	public static List<String[]> getSplitDates(String startDateStr, String endDateStr, int splitSize) {
		DateTime startDate = new DateTime(startDateStr);
		DateTime endDate = new DateTime(endDateStr);
		
		//get holidays
		//List<String> holidays = getHolidaysAndWeekends(2013);

		//calculate days
		int days = getDaysBetween(startDate, endDate);
		//System.out.println("days: " + days);
		
		//get working days
		List<String> dateList = new ArrayList<String>();
		for(int i = 0; i < days; i++) {
			String date = startDate.format(FORMAT_PATTERN);
			/*boolean isHoliday = validate(date, holidays);
			if(!isHoliday){
				dateList.add(date);
			}*/
			dateList.add(date);
			startDate = startDate.plusDays(1);
		}
		
		//get split days
		List<String[]> resultList = new ArrayList<String[]>();
		List<List<String>> splits = splitList(dateList, splitSize);
		for(List<String> split: splits) {
			//only get the first and the last one
			resultList.add(new String[] {split.get(0), split.get(split.size() - 1)});
		}
		return resultList;
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
		//System.out.println("holidays: " + holidays);

		//calculate days
		int days = getDaysBetween(startDate, endDate);
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
	
	public static DateTime getIntervalWorkingDay(DateTime startDate, int interval, boolean isNext) {
		DateTime endDate = startDate.minusDays(interval * 2);
		//get holidays
		List<String> holidays = getHolidaysAndWeekendsByRange(startDate, endDate);

		//get working days
		while(interval > 0) {
			String date = startDate.format(FORMAT_PATTERN);
			boolean isHoliday = validate(date, holidays);
			if(!isHoliday){
				interval--;
			}
			startDate = isNext ? startDate.plusDays(1) : startDate.minusDays(1);
		}
		return isNext ? startDate.minusDays(1) : startDate.plusDays(1);
	}
	
	private static int getDaysBetween(DateTime startDate, DateTime endDate) {
		return ( endDate.getDayOfYear() - startDate.getDayOfYear() + 1 );
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
