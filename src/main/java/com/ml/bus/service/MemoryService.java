package com.ml.bus.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.StockDAO;
import com.ml.model.StockCode;
import com.ml.util.Constants;


@Service
public class MemoryService {
	
	@Autowired
	StockDAO stockDAO;
	
	private Map<String, String> ceStockCodes;
	private String strategys;
	private List<String> stockCodes;
	
	@PostConstruct 
    public void init() throws IOException{ 
		List<StockCode> ceCodeList = stockDAO.findAllStockCodes();
		ceStockCodes = new HashMap<String, String>(ceCodeList.size());
		for(StockCode sc: ceCodeList) {
			ceStockCodes.put(sc.getCode(), sc.getName());
		}
		setStrategys("StrategyA,StrategyB,StrategyC,StrategyD");
		stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
    }
	
	public Map<String, String> getCeStockCodes() {
		return ceStockCodes;
	}

	public void setCeStockCodes(Map<String, String> ceStockCodes) {
		this.ceStockCodes = ceStockCodes;
	}

	public List<String> getStockCodes() {
		return stockCodes;
	}
	public void setStockCodes(List<String> stockCodes) {
		this.stockCodes = stockCodes;
	}
	public String getStrategys() {
		return strategys;
	}
	public void setStrategys(String strategys) {
		this.strategys = strategys;
	}
}
