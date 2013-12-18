package com.ml.bus.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ml.bus.dao.MatchResultDAO;
import com.ml.bus.dao.StockDAO;
import com.ml.model.ShareHolder;
import com.ml.model.StockCode;
import com.ml.util.Constants;


@Service
public class MemoryService {
	
	@Autowired
	StockDAO stockDAO;
	
	@Autowired
	MatchResultDAO matchResultDAO;
	
	private Map<String, String> mapCeStockCodes;
	private List<StockCode> ceStockCodes;

	private String strategys;
	private List<String> stockCodes;
	
	private Map<String, TreeSet<ShareHolder>> shareHolders;
	
	@PostConstruct 
    public void init() throws IOException{ 
		ceStockCodes = stockDAO.findAllStockCodes();
		mapCeStockCodes = new HashMap<String, String>(ceStockCodes.size());
		for(StockCode sc: ceStockCodes) {
			mapCeStockCodes.put(sc.getCode(), sc.getName());
		}
		setStrategys("StrategyA,StrategyB,StrategyC,StrategyD");
		stockCodes = FileUtils.readLines(new File(Constants.CorpCodesFile));
		
		List<ShareHolder> shs = matchResultDAO.findAllSH();
		shareHolders = new HashMap<String, TreeSet<ShareHolder>>(shs.size());
		for(ShareHolder sh: shs) {
			TreeSet<ShareHolder> tsh = shareHolders.get(sh.getCode());
			if(tsh == null) {
				tsh = new TreeSet<ShareHolder>();
			}
			tsh.add(sh);
			shareHolders.put(sh.getCode(), tsh);
		}
    }
	
	public Map<String, TreeSet<ShareHolder>> getShareHolders() {
		return shareHolders;
	}
	public void setShareHolders(Map<String, TreeSet<ShareHolder>> shareHolders) {
		this.shareHolders = shareHolders;
	}
	public Map<String, String> getMapCeStockCodes() {
		return mapCeStockCodes;
	}
	public void setMapCeStockCodes(Map<String, String> mapCeStockCodes) {
		this.mapCeStockCodes = mapCeStockCodes;
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
	public List<StockCode> getCeStockCodes() {
		return ceStockCodes;
	}
	public void setCeStockCodes(List<StockCode> ceStockCodes) {
		this.ceStockCodes = ceStockCodes;
	}
	
}
