package com.ml.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;

public class SimpleHttpCrawler implements Crawler {

	@Override
	public ResultItems crawl(Site site) {
		HttpURLConnection httpConn = null;
		InputStream in = null;
		try {
			URL url = new URL(site.getUrl());
			httpConn = (HttpURLConnection) url.openConnection();
			httpConn.setRequestMethod("GET");
			in = httpConn.getInputStream();
			return handleResponse(site.getCharset(), in);
		
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				in.close();
			} catch (IOException e) {
			} catch (NullPointerException e) {
			}
			httpConn.disconnect();
		}
		return null;
	}
	
	private ResultItems handleResponse(String charset, InputStream in) throws IOException {
        String content = IOUtils.toString(in, charset);
        if(content.equals(""))
        	return null;
        ResultItems resultItems = new ResultItems(content);
        return resultItems;
    }
	
	@Override
	public void setThread(int threadNum) {
	}

}
