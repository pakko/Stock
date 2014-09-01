package com.ml.bus.controller;


import hirondelle.date4j.DateTime;

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
import com.ml.model.DdxStock;
import com.ml.model.MatchResult;
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
    	
    	DateTime d = DateUtil.getIntervalWorkingDay(new Date().getTime(), 1, false);
    	long date = DateUtil.getMilliseconds(d);
		
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
		
		for(MatchResult mr: mrs){
			Map<Object, Object> row = new HashMap<Object, Object>();
			String stockCode = mr.getCode();
			long mrDate = mr.getDate();
			double d5 = mr.getD5();
			double d10 = mr.getD10();
			double d20 = (mr.getD20() != null) ? mr.getD20() : 0;
			double d30 = (mr.getD30() != null) ? mr.getD30() : 0;
			double dnow = mr.getDnow();
			
			row.put("code", stockCode);
			row.put("name", stockCodes.get(stockCode));
			if(mr.getStrategy().equals("StrategyF")) {
				row.put("date", mr.getFlyDate());
				row.put("flyDate", mrDate);
			}
			else {
				row.put("date", mrDate);
			}
			row.put("strategy", mr.getStrategy());
			row.put("ddx", ddxMap.get(stockCode));
			row.put("d5", d5);
			row.put("d10", d10);
			row.put("d20", d20);
			row.put("d30", d30);
			row.put("dnow", dnow);
			row.put("sh", stats.get(stockCode));
        	rows.add(row);
		}
		res.put("cell", rows);
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
