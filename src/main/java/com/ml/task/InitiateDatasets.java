package com.ml.task;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;

import com.ml.db.MongoDB;
import com.ml.model.StockCode;
import com.ml.util.Constants;

public class InitiateDatasets {

	public static MongoDB getDB() throws FileNotFoundException, IOException {
		String confFile = Constants.DefaultConfigFile;
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);
		return mongodb;
	}
	
	public static void saveStockCode(List<String> stockCodes, MongoDB mongodb) throws IOException {
		Map<String, String> map = TaskResemble.getStockCodes();
		for(String code: stockCodes) {
			StockCode sc = new StockCode(code, map.get(code.substring(2)));
			if(map.get(code.substring(2)) == null)
					System.out.println(sc);
			mongodb.save(sc, "stockCode");
		}
	}
	
	public static void main(String[] args) throws Exception {
		MongoDB mongodb = getDB();
		
		// stock codes
		List<String> stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		System.out.println("Corp code size: " + stockCodes.size());
		
		saveStockCode(stockCodes, mongodb);
		
	}

}
