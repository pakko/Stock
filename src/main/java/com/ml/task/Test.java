package com.ml.task;

import hirondelle.date4j.DateTime;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import com.ml.util.DateUtil;

public class Test {
	

	public static void main(String[] args) throws IOException {
		/*List<String> lines = FileUtils.readLines(new File("c:\\tt.txt"));
		for(String line: lines) {
			String[] strs = line.split(" ");
			System.out.println("slected.put(\""+ strs[0]+"\", \""+ strs[1]+"\");");
		}*/
		double price = 5;
		double ma5 = 3;
		double ma10 = 3;
		double ma20 = 2;
		double ma30 = 1;
		boolean isAvgPriceGood = (price >= ma5) ? ((ma5 >= ma10) ? ( (ma10 >= ma20) ? (ma20 >= ma30) : false) : false ) : false;
		System.out.println(isAvgPriceGood);
	}

}
