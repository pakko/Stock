package com.ml.bus.controller;


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
import com.ml.bus.service.TransferStockService;
import com.ml.model.ScenarioResult;
import com.ml.util.Pagination;


@Controller
@RequestMapping(value = "/transfer")
public class TransferController {

    @Autowired
    private TransferStockService transferStockService;
    
    @Autowired
    private MemoryService memoryService;
    
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
    		pager = transferStockService.findByPageAndStockCodeAndDate(pager, code, startDate, endDate);
    	}
    	else {
    		pager = transferStockService.findByPageAndStockCode(pager, code);
    	}
    	
    	@SuppressWarnings("unchecked")
		List<ScenarioResult> srs = (List<ScenarioResult>) pager.getItems();
    	
    	Map<String, Object> result = new HashMap<String, Object>();
		result.put("total", pager.getTotalPage());
		result.put("page", pager.getCurrentPage());
		result.put("records", pager.getTotalCount());
		result.put("rows", srs);
		
		return result;
    }
}
