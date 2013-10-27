package com.ml.util;

import java.text.NumberFormat;
import java.text.ParseException;

public class NumberUtil {

	public static Double formatNumber(String numberStr) {
		NumberFormat nf = NumberFormat.getInstance();
		Object res = null;
        try {
			res = nf.parse(numberStr);
			if(res instanceof Long) {
				String resStr = String.valueOf(res);
				return Double.valueOf(resStr);
			}
			else if(res instanceof Double) {
				return (Double) res;
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
        return null;
	}
}
