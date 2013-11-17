package com.ml.bus.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ml.bus.service.MatchResultService;
import com.ml.bus.service.MemoryService;
import com.ml.model.MatchResult;


@Controller
@RequestMapping(value = "/result")
public class MatchResultController {

    @Autowired
    private MatchResultService matchResultService;
    
    @Autowired
    private MemoryService memoryService;
    
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<Map<Object, Object>> getResult2(
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate) {
    	
    	List<MatchResult> mrs;
    	if(startDate != null && !startDate.equals("")
    			&& endDate != null && !endDate.equals("")) {
    		mrs = matchResultService.findByDate(startDate, endDate);
    	}
    	else {
    		mrs = matchResultService.findAll();
    	}

		Map<String, String> stockCodes = memoryService.getCeStockCodes();
		
		List<Map<Object, Object>> rows = new ArrayList<Map<Object, Object>>();
		for(MatchResult mr: mrs){
			Map<Object, Object> row = new HashMap<Object, Object>();
			row.put("code", mr.getCode());
			row.put("name", stockCodes.get(mr.getCode()));
			row.put("date", mr.getDate());
			row.put("strategy", mr.getStrategy());
        	rows.add(row);
		}
		
		return rows;
    }
}
