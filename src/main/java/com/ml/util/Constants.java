package com.ml.util;


public class Constants {
	
	public static final String separator = "/";
	public static String CurrentDir = Constants.class.getResource("/").getPath();

	public static final String DefaultConfigFile = CurrentDir + separator + "default.properties";
	public static final String CorpCodesFile = CurrentDir + separator + "corp_codes.csv";
	
	public static final String StockCollectionName = "stock";
	public static final String ScenarioResultCollectionName = "scenarioResult";
	public static final String MatchResultCollectionName = "matchResult";
	public static final String StatsResultCollectionName = "statsResult";

	public static final int SplitDays = 100;
	public static final int BaseDays = 100;

}
