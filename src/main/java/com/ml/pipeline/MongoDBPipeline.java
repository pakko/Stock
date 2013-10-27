package com.ml.pipeline;

import org.apache.http.annotation.ThreadSafe;

import com.ml.db.MongoDB;

import java.util.List;

@ThreadSafe
public class MongoDBPipeline<T> implements Pipeline<T> {
	private MongoDB mongodb;
	private String collectionName;

    public MongoDBPipeline(MongoDB mongodb, String collectionName) {
        this.mongodb = mongodb;
        this.collectionName = collectionName;
    }

    @Override
    public void process(List<T> datas) {
    	if(datas == null || datas.size() <= 0)
    		return;
    	mongodb.insert(datas, collectionName);
    }
}
