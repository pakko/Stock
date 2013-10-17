package com.ml.stock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.ml.db.MongoDB;
import com.ml.util.Constants;
import com.ml.util.DateSplit;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

public class RetrieveStockData {
	private static DateFormat sdf;
	static {
		String pattern = "yyyy-MM-dd";
		TimeZone timeZone = TimeZone.getTimeZone("GMT+8:00");
		sdf = new SimpleDateFormat(pattern);
		sdf.setTimeZone(timeZone);
	}
	
	public static void main(String[] args) throws Exception {
		String stockCode = "zs_000001";
		String beginDate = "2013-01-01";
		String endDate = "2013-10-16";
		String periodMethod = "d";
		String resultMethod = "js";

		// stock codes
		List<String> lines = FileUtils.readLines(new File(Constants.corpCodesFile));
		System.out.println("Corp code size: " + lines.size());

		// initial mongodb
		String confFile = Constants.defaultConfigFile;
		if(args.length > 0) {
			confFile = args[0];
		}
		Properties props = new Properties();
		props.load(new FileInputStream(confFile));
		MongoDB mongodb = new MongoDB(props);

		RetrieveStockData rsd = new RetrieveStockData();
		
		// get split date list
		List<String[]> lists = DateSplit.split(beginDate, endDate);
		if (!lists.isEmpty()) {
			for (String[] dates : lists) {
				System.out.println("start date: " + dates[0] + ", end date: " + dates[1]);
				for (String line : lines) {
					stockCode = "cn_" + line.split(",")[0];
					rsd.process(mongodb, stockCode, dates[0], dates[1], periodMethod, resultMethod);
				}
			}
		}
	}
	
	public void process(MongoDB mongodb, String stockCode, String beginDate, 
			String endDate, String periodMethod, String resultMethod) {
		
		String getUrl = "http://q.stock.sohu.com/app2/history.up?method=history"
				+ "&code=" + stockCode
				+ "&sd=" + beginDate
				+ "&ed=" + endDate
				+ "&t=" + periodMethod 
				+ "&res=" + resultMethod;
		try {
			// get data and remove some tags
			String result = downLoadPages(getUrl);
			result = this.formatUrlResult(result);

			// use jackson to translate string to json list
			ObjectMapper objectMapper = new ObjectMapper();
			List<List<String>> lists = objectMapper.readValue(result, List.class);
			
			// translate lists to object
			Set<Stock> stockList = listToResultBean(lists);

			// save to db
			this.saveToDB(mongodb, stockCode, stockList);
			
		} catch (ParseException e) {
			System.err.println("Parse, Stock code:" + stockCode + "---" + e.getMessage());
		} catch (IOException e) {
			System.err.println("Download, Stock code:" + stockCode + "---" + e.getMessage());
		}
	}

	private void saveToDB(MongoDB mongodb, String stockCode, Set<Stock> stockList) {
		Query query = new Query();
		query.addCriteria(Criteria.where("stockCode").is(stockCode));
		List<Stocks> results = mongodb.find(query, Stocks.class, Constants.stockCollectionName);
		if(results != null && results.size() > 0) {
			stockList.addAll(results.get(0).getStocks());
		}
		Stocks stocks = new Stocks(stockCode, stockList);
		mongodb.save(stocks, Constants.stockCollectionName);
	}
	
	private String formatUrlResult(String result) {
		result = result.trim();
		result = result.replace("\n", "");
		result = result.substring("PEAK_ODIA(['hq_history',".length(),
				result.length() - 1);
		result = result.replace("'", "\"");

		//System.out.println(result);
		return result;
	}

	private Set<Stock> listToResultBean(List<List<String>> lists)
			throws ParseException {
		Set<Stock> stockList = new TreeSet<Stock>();
		for (List<String> list : lists) {
			Date date = sdf.parse(list.get(0).toString());
			double openSpan = Double.parseDouble(list.get(1).toString());
			double closeSpan = Double.parseDouble(list.get(2).toString());
			double upMount = Double.parseDouble(list.get(3).toString());
			double upBandth = Double.parseDouble(list.get(4).toString()
					.substring(0, list.get(4).toString().length() - 1));
			double min = Double.parseDouble(list.get(5).toString());
			double max = Double.parseDouble(list.get(6).toString());
			long dealMount = Long.parseLong(list.get(7).toString());
			double dealMoney = Double.parseDouble(list.get(8).toString());
			double changeRate = 0;
			if (!list.get(9).toString().contains("-")) {
				changeRate = Double.parseDouble(list.get(9).toString()
						.substring(0, list.get(9).toString().length() - 1));
			}
			Stock stock = new Stock(date, openSpan, closeSpan, upMount,
					upBandth, min, max, dealMount, dealMoney, changeRate);
			stockList.add(stock);
			// System.out.println(stock.toString());
		}
		return stockList;
	}

	private String downLoadPages(String urlStr) throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
		httpConn.setRequestMethod("GET");
		InputStream in = httpConn.getInputStream();
		String content = IOUtils.toString(in, "GBK");
		// System.out.println(content);
		in.close();
		httpConn.disconnect();

		return content;
	}

}
