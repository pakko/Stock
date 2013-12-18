package com.ml.util;


public class Constants {
	
	public static final String separator = "/";
	public static String CurrentDir = Constants.class.getResource("/").getPath();

	public static final String DefaultConfigFile = CurrentDir + separator + "default.properties";
	public static final String CorpCodesFile = CurrentDir + separator + "corp_codes.txt";
	public static final String CECodesFile = CurrentDir + separator + "ce_codes.txt";

	public static final String StockCollectionName = "stock";
	public static final String ShareCapitalCollectionName = "shareCapital";
	public static final String RealStockCollectionName = "realStock";
	public static final String DDZStockCollectionName = "ddzStock";
	public static final String StockCodeCollectionName = "stockCode";

	public static final String ScenarioResultCollectionName = "scenarioResult";
	public static final String MatchResultCollectionName = "matchResult";
	public static final String StatsResultCollectionName = "statsResult";
	public static final String ShareHolderCollectionName = "shareHolder";
	
	public static final int BaseDays = 250;
	

}
