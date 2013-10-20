package com.ml.qevent;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.ml.db.MongoDB;
import com.ml.task.TransferDataTask;

public class TransferDataListener implements QueueListener {

	private MongoDB mongodb;
	private List<String> stockCodes;
	private List<List<String>> datas;
	private ExecutorService service;
	
	public TransferDataListener(MongoDB mongodb, List<String> stockCodes, 
			List<List<String>> datas, ExecutorService service) {
		this.mongodb = mongodb;
		this.stockCodes = stockCodes;
		this.datas = datas;
		this.service = service;
	}

	@Override
	public void queueEvent(QueueEvent event) {
		if (event.getQueueState() != null && event.getQueueState().equals("take_retrieved_data")) {
			for (List<String> data : datas) {
				TransferDataTask tdt = new TransferDataTask(mongodb, stockCodes, data);
				service.submit(tdt);
			}
			service.shutdown();
        }
	}

}
