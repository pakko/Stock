package com.ml.strategy;

public class Context {
    //持有一个具体策略的对象
    private Strategy strategy;
    private boolean isReal;
    
    public Context(Strategy strategy){
        this.strategy = strategy;
    }
    /**
     * 策略方法
     */
    public int calculate(String stockCode, String theDate){
    	return strategy.calculate(stockCode, theDate);
    }
	public Strategy getStrategy() {
		return strategy;
	}
	public void setStrategy(Strategy strategy) {
		this.strategy = strategy;
	}
	public boolean isReal() {
		return isReal;
	}
	public void setReal(boolean isReal) {
		this.isReal = isReal;
	}
    
    
}