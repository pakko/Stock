package com.ml.util;


public class Constants {
	
	public static final String separator = "/";
	public static String currentDir = Constants.class.getResource("/").getPath();

	public static final String defaultConfigFile = currentDir + separator + "default.properties";
	public static final String corpCodesFile = currentDir + separator + "corp_codes.csv";
	
	public static final String stockCollectionName = "stocks";
	
	public static final int splitDays = 100;

}
