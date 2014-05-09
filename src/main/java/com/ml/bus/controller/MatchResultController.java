package com.ml.bus.controller;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
import com.ml.model.DdxStock;
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
    	
    	// get the day before today
		Calendar c1 = Calendar.getInstance();
		int year = c1.get(Calendar.YEAR);
        int month = c1.get(Calendar.MONTH);
        int day = c1.get(Calendar.DATE);
        long c1t = c1.getTimeInMillis();
        
        Calendar c2 = Calendar.getInstance();
		c2.set(year, month, day, 15, 30);
		long c2t = c2.getTimeInMillis();
		
		if(c1t < c2t)
			c1.add(Calendar.DATE, -1);
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		String theDate = sdf.format(c1.getTime());
		long date = DateUtil.getMilliseconds(theDate);
		
		// get ddx 
		List<DdxStock> ddxs = matchResultService.findDDXByDate(date);
    	Map<String, Double> ddxMap = new HashMap<String, Double>(ddxs.size());
    	for(DdxStock ddx: ddxs) {
    		ddxMap.put(ddx.getCode(), ddx.getDdx());
    	}
    	
		Map<String, String> stockCodes = memoryService.getMapCeStockCodes();
		Map<String, Integer> stats = memoryService.getShStats();
		
		Map<Object, Object> res = new HashMap<Object, Object>();
		List<Map<Object, Object>> rows = new ArrayList<Map<Object, Object>>();
		int s5 = 0;
		int s10 = 0;
		int snow = 0;
		int ta = 0;
		int td = 0;
		int tas5 = 0;
		int tas10 = 0;
		int tasnow = 0;
		int tds5 = 0;
		int tds10 = 0;
		int tdsnow = 0;
		for(MatchResult mr: mrs){
			Map<Object, Object> row = new HashMap<Object, Object>();
			String stockCode = mr.getCode();
			long mrDate = mr.getDate();
			double d5 = mr.getD5();
			double d10 = mr.getD10();
			double dnow = mr.getDnow();
			
			// calculate the now price diff
			double dp = 0;
			ScenarioResult stockNow = transferStockService.findByStockCodeAndDate(stockCode, date);
			if(stockNow != null) {
				dp = (stockNow.getNowPrice() - dnow) / dnow;
			}
			
			if(d5 > 0)
				s5++;
			if(d10 > 0)
				s10++;
			if(dp > 0)
				snow++;
			if(mr.getStrategy().equals("StrategyD")) {
				td++;
				if(d5 > 0)
					tds5++;
				if(d10 > 0)
					tds10++;
				if(dp > 0)
					tdsnow++;
			}
			if(mr.getStrategy().equals("StrategyA")) {
				ta++;
				if(d5 > 0)
					tas5++;
				if(d10 > 0)
					tas10++;
				if(dp > 0)
					tasnow++;
			}
			row.put("code", stockCode);
			row.put("name", stockCodes.get(stockCode));
			row.put("date", mrDate);
			row.put("strategy", mr.getStrategy());
			row.put("ddx", ddxMap.get(stockCode));
			row.put("d5", d5);
			row.put("d10", d10);
			row.put("dnow", dp);
			row.put("sh", stats.get(stockCode));
        	rows.add(row);
		}
		res.put("cell", rows);
		res.put("s5", s5);
		res.put("s10", s10);
		res.put("snow", snow);
		int total = rows.size();
		System.out.println("s5: " + s5 + ", per: " + s5 * 1.0/ total);
		System.out.println("s10: " + s10 + ", per: " + s10 * 1.0/ total);
		System.out.println("snow: " + snow + ", per: " + snow * 1.0/ total);
		System.out.println("tas5: " + tas5 + ", per: " + tas5 * 1.0/ ta);
		System.out.println("tas10: " + tas10 + ", per: " + tas10 * 1.0/ ta);
		System.out.println("tasnow: " + tasnow + ", per: " + tasnow * 1.0/ ta);
		System.out.println("tds5: " + tds5 + ", per: " + tds5 * 1.0/ td);
		System.out.println("tds10: " + tds10 + ", per: " + tds10 * 1.0/ td);
		System.out.println("tdsnow: " + tdsnow + ", per: " + tdsnow * 1.0/ td);
		return rows;
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
