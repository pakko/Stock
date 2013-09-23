package com.ml.db;

import java.net.UnknownHostException;
import java.util.List;


import java.util.Properties;

import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

public class MongoDB {

	MongoTemplate mongoTemplate;
	
	public MongoDB(Properties props) {
		try {
			String host = props.getProperty("host");
			int port = Integer.valueOf(props.getProperty("port"));
			String db = props.getProperty("database");
			String user = props.getProperty("user");
			String password = props.getProperty("password");
			
			UserCredentials userCredentials = new UserCredentials(user, password);
			Mongo mongo = new Mongo(host, port);
			mongoTemplate = new MongoTemplate(mongo, db, userCredentials);
			
		} catch (UnknownHostException e) {
			System.out.println(e.toString());
		} catch (MongoException e) {
			System.out.println(e.toString());
		}
	}
	
	
	public MongoDB(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	
	public void save(Object entity) {
		mongoTemplate.save(entity);
	}

	
	public void save(Object entity, String collectionName) {
		mongoTemplate.save(entity, collectionName);
	}

	
	public void update(Object entity) {
		mongoTemplate.save(entity);
	}

	
	public void update(Object entity, String collectionName) {
		mongoTemplate.save(entity, collectionName);
	}

	
	public void delete(Object entity) {
		mongoTemplate.remove(entity);
	}

	
	public void delete(Object entity, String collectionName) {
		mongoTemplate.remove(entity, collectionName);
	}

	
	public <T> List<T> find(Object query, Object entity) {
		return mongoTemplate.find((Query)query, (Class<T>) entity);
	}

	
	public <T> List<T> find(Object query, Object entity, String collectionName) {
		return mongoTemplate.find((Query)query, (Class<T>) entity, collectionName);
	}

	
	public <T> T findOne(Object query, Object entity) {
		return mongoTemplate.findOne((Query)query, (Class<T>) entity);
	}

	
	public <T> T findOne(Object query, Object entity, String collectionName) {
		return mongoTemplate.findOne((Query)query, (Class<T>) entity, collectionName);
	}

	
	public <T> List<T> findAll(Object entity) {
		return mongoTemplate.findAll((Class<T>) entity);
	}

	
	public <T> List<T> findAll(Object entity, String collectionName) {
		return mongoTemplate.findAll((Class<T>) entity, collectionName);
	}

	
	public long count(Object query, String collectionName) {
		return mongoTemplate.count((Query) query, collectionName);
	}
	
	public MongoTemplate getMongoTemplate() {
		
		return mongoTemplate;
	}

	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	


}
