package com.ml.pipeline;


import java.util.List;
public class MemoryPipeline<T> implements Pipeline<T> {
	private List<T> objects;
	
    @Override
    public void process(List<T> datas) {
    	objects.addAll(datas);
    }

	public List<T> getObjects() {
		return objects;
	}
    
    
}
