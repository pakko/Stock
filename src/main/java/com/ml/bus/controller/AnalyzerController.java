package com.ml.bus.controller;


import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import com.ml.bus.service.AnalyzerResultService;
import com.ml.bus.service.MatchResultService;
import com.ml.bus.service.MemoryService;
import com.ml.bus.service.TransferStockService;
import com.ml.model.AnalyzerResult;
import com.ml.model.DdxStock;
import com.ml.model.MatchResult;
import com.ml.model.ScenarioResult;
import com.ml.model.ShareHolder;
import com.ml.util.DateUtil;


@Controller
@RequestMapping(value = "/analyzer")
public class AnalyzerController {

    @Autowired
    private AnalyzerResultService analyzerResultService;
    
    @Autowired
    private MemoryService memoryService;
    
    @Autowired
    private TransferStockService transferStockService;

    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody List<AnalyzerResult> getResult(
    		@RequestParam(value = "code", required = false) String code,
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate) {
    	
    	List<AnalyzerResult> mrs;
    	if(startDate != null && !startDate.equals("")
    			&& endDate != null && !endDate.equals("")) {
    		if(code != null && !code.equals("")) {
    			mrs = analyzerResultService.findByCodeAndDate(code, startDate, endDate);
        	}
    		else {
    			mrs = analyzerResultService.findByDate(startDate, endDate);
    		}
    	}
    	else {
    		if(code != null && !code.equals("")) {
    			mrs = analyzerResultService.findByCode(code);
        	}
    		else {
    			mrs = analyzerResultService.findAll();
    		}
    	}
		
		return mrs;
    }
}
