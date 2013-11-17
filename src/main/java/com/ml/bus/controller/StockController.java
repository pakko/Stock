package com.ml.bus.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ml.bus.service.MemoryService;
import com.ml.bus.service.StockService;
import com.ml.model.Stock;
import com.ml.util.Pagination;


@Controller
@RequestMapping(value = "/stock")
public class StockController {

    @Autowired
    private StockService stockService;
    
    @Autowired
    private MemoryService memoryService;
    
    @RequestMapping(value = "/code", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getStockCode() {
    	return memoryService.getCeStockCodes();
    }
    
    @RequestMapping(value = "/strategy", method = RequestMethod.GET)
    public @ResponseBody List<String> getStrategys() {
    	String strategys = memoryService.getStrategys();
    	List<String> results = new ArrayList<String>();
    	for(String str: strategys.split(",")) {
    		results.add(str);
    	}
    	return results;
    }
    
    @RequestMapping(value = "", method = RequestMethod.POST, produces = "application/json;charset=UTF-8")
    public @ResponseBody Map<String, Object> getStock(
    		@RequestParam(value = "code", required = false) String code,
    		@RequestParam(value = "startDate", required = false) String startDate,
    		@RequestParam(value = "endDate", required = false) String endDate,
    		HttpServletRequest servletRequest) {
    	
    	if(code == null || code.equals(""))
    		return null;
    	Pagination pager = new Pagination(servletRequest);
    	if(startDate != null && !startDate.equals("")
    			&& endDate != null && !endDate.equals("")) {
    		pager = stockService.findByPageAndStockCodeAndDate(pager, code, startDate, endDate);
    	}
    	else {
    		pager = stockService.findByPageAndStockCode(pager, code);
    	}
    	
    	@SuppressWarnings("unchecked")
		List<Stock> stocks = (List<Stock>) pager.getItems();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pager.getTotalPage());
		result.put("page", pager.getCurrentPage());
		result.put("records", pager.getTotalCount());
		result.put("rows", stocks);
		
		return result;
    }
}
