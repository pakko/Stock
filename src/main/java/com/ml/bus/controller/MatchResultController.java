package com.ml.bus.controller;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ml.bus.service.MatchResultService;
import com.ml.bus.service.MemoryService;
import com.ml.bus.service.TransferStockService;
import com.ml.model.DdzStock;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.model.ShareHolder;
import com.ml.util.DateUtil;


@Controller
@RequestMapping(value = "/result")
public class MatchResultController {

    @Autowired
    private MatchResultService matchResultService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Autowired
    private TransferStockService transferStockService;
    
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Map<Object, Object>> getResult(
    		@RequestParam(value = "code", required = false) String code,
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate) {
    	
    	List<MatchResult> mrs;
    	if(startDate != null && !startDate.equals("")
    			&& endDate != null && !endDate.equals("")) {
    		if(code != null && !code.equals("")) {
    			mrs = matchResultService.findByCodeAndDate(code, startDate, endDate);
        	}
    		else {
    			mrs = matchResultService.findByDate(startDate, endDate);
    		}
    	}
    	else {
    		if(code != null && !code.equals("")) {
    			mrs = matchResultService.findByCode(code);
        	}
    		else {
    			mrs = matchResultService.findAll();
    		}
    	}
    	
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String theDate = dateFormat.format(new Date());
		
		Map<String, String> stockCodes = memoryService.getMapCeStockCodes();
		Map<String, Integer> stats = memoryService.getShStats();
		
		List<Map<Object, Object>> rows = new ArrayList<Map<Object, Object>>();
		for(MatchResult mr: mrs){
			Map<Object, Object> row = new HashMap<Object, Object>();
			String stockCode = mr.getCode();
			long date = mr.getDate();
			DateTime beforeDate5 = DateUtil.getIntervalWorkingDay(date, 5, true);
			DateTime beforeDate10 = DateUtil.getIntervalWorkingDay(date, 10, true);
			
			double d = 0;
			double d5 = 0; 
			double d10 = 0; 
			double dnow = 0;
			ScenarioResult stock = transferStockService.findByStockCodeAndDate(stockCode, date);
			if(stock != null) {
				d = stock.getNowPrice();
			}
			ScenarioResult stock5 = transferStockService.findByStockCodeAndDate(stockCode, DateUtil.getMilliseconds(beforeDate5));
			if(stock5 != null) {
				d5 = (stock5.getNowPrice() - d) / d;
			}
			ScenarioResult stock10 = transferStockService.findByStockCodeAndDate(stockCode, DateUtil.getMilliseconds(beforeDate10));
			if(stock10 != null) {
				d10 = (stock10.getNowPrice() - d) / d;
			}
			ScenarioResult stockNow = transferStockService.findByStockCodeAndDate(stockCode, DateUtil.getMilliseconds(theDate));
			if(stockNow != null) {
				dnow = (stockNow.getNowPrice() - d) / d;
			}
			
			row.put("code", stockCode);
			row.put("name", stockCodes.get(stockCode));
			row.put("date", date);
			row.put("strategy", mr.getStrategy());
			row.put("ddx", getDDX(stockCode, date));
			row.put("d5", d5);
			row.put("d10", d10);
			row.put("dnow", dnow);
			row.put("sh", stats.get(stockCode));
        	rows.add(row);
		}
		
		return rows;
    }
    
    private double getDDX(String stockCode, long theDateSecs) {
		DateTime beforeDate = DateUtil.getIntervalWorkingDay(theDateSecs, 20, false);
		long beforeDateSecs = DateUtil.getMilliseconds(beforeDate);

		List<DdzStock> ddzStockList = matchResultService.findDDZByDate(stockCode, beforeDateSecs, theDateSecs);
		double sum = 0.0;
		for(int i = 1; i < ddzStockList.size(); i++) {
			DdzStock ds = ddzStockList.get(i);
			sum += (ds.getIn() + ds.getOut());
		}
		return sum;
	}
    
    @RequestMapping(value = "/sh", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Map<Object, Object>> getSH() {
    	
		Map<String, TreeSet<ShareHolder>> shareHolders = memoryService.getShareHolders();
		
		Map<String, Integer> stats = memoryService.getShStats();
		Map<String, String> stockCodes = memoryService.getMapCeStockCodes();
		List<Map<Object, Object>> rows = new ArrayList<Map<Object, Object>>();
		for(String key: stats.keySet()){
			Map<Object, Object> row = new HashMap<Object, Object>();
			row.put("code", key);
			row.put("name", stockCodes.get(key));
			row.put("days", stats.get(key));
			row.put("sh", shareHolders.get(key).toString());
        	rows.add(row);
		}
		
		return rows;
    }
}
