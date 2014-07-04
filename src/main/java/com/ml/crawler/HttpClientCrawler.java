package com.ml.crawler;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpResponse;
import org.apache.http.annotation.ThreadSafe;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.methods.HttpGet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ml.util.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ThreadSafe
public class HttpClientCrawler implements Crawler {
	private static final Logger logger = LoggerFactory.getLogger(HttpClientCrawler.class);

    private HttpClientPool httpClientPool;

    private int poolSize = 10;


    private HttpClientPool getHttpClientPool(){
        if (httpClientPool==null){
            httpClientPool = new HttpClientPool(poolSize);
        }
        return httpClientPool;
    }

    @Override
    public ResultItems crawl(Site site) {
        Set<Integer> acceptStatCode;
        String charset = null;
        Map<String,String> headers = null;
        if (site != null) {
            acceptStatCode = site.getAcceptStatCode();
            charset = site.getCharset();
            headers = site.getHeaders();
        } else {
            acceptStatCode = new HashSet<Integer>();
            acceptStatCode.add(200);
        }
        HttpClient httpClient = getHttpClientPool().getClient(site);
        try {
            HttpGet httpGet = new HttpGet(site.getUrl());
            if (headers!=null){
                for (Map.Entry<String, String> headerEntry : headers.entrySet()) {
                    httpGet.addHeader(headerEntry.getKey(),headerEntry.getValue());
                }
            }
            HttpResponse httpResponse = httpClient.execute(httpGet);
            
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            if (acceptStatCode.contains(statusCode)) {
                handleGzip(httpResponse);
                //charset
                if (charset == null) {
                    String value = httpResponse.getEntity().getContentType().getValue();
                    charset = UrlUtils.getCharset(value);
                }
                return handleResponse(charset, httpResponse.getEntity().getContent());
            } else {
                logger.warn("code error " + statusCode + "\t" + site.getUrl());
            }
        } catch (Exception e) {
            logger.warn("download page " + site.getUrl() + " error", e);
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
    public void setThread(int thread) {
        poolSize = thread;
        httpClientPool = new HttpClientPool(thread);
    }

    private void handleGzip(HttpResponse httpResponse) {
        Header ceheader = httpResponse.getEntity().getContentEncoding();
        if (ceheader != null) {
            HeaderElement[] codecs = ceheader.getElements();
            for (HeaderElement codec : codecs) {
                if (codec.getName().equalsIgnoreCase("gzip")) {
                    httpResponse.setEntity(
                            new GzipDecompressingEntity(httpResponse.getEntity()));
                }
            }
        }
    }
}
