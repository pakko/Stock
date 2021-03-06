package com.ml.crawler;

import org.apache.http.HttpVersion;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.params.*;

import java.util.Map;

public class HttpClientPool {

    private int poolSize;

    private PoolingClientConnectionManager connectionManager;

    public PoolingClientConnectionManager getConnectionManager() {
		return connectionManager;
	}

	public void setConnectionManager(
			PoolingClientConnectionManager connectionManager) {
		this.connectionManager = connectionManager;
	}

	public HttpClientPool(int poolSize) {
        this.setPoolSize(poolSize);
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));
        schemeRegistry.register(new Scheme("https", 443, SSLSocketFactory.getSocketFactory()));

        connectionManager = new PoolingClientConnectionManager(schemeRegistry);
        connectionManager.setMaxTotal(poolSize);
        connectionManager.setDefaultMaxPerRoute(100);
    }

    public HttpClient getClient(Site site) {
        return generateClient(site);
    }

    private HttpClient generateClient(Site site) {
        HttpParams params = new BasicHttpParams();
        if (site != null && site.getUserAgent() != null) {
            params.setParameter(CoreProtocolPNames.USER_AGENT, site.getUserAgent());
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, site.getTimeOut());
            params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, site.getTimeOut());
        } else {
            params.setIntParameter(CoreConnectionPNames.SO_TIMEOUT, 3000);
            params.setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 3000);
        }

        params.setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BEST_MATCH);
        HttpProtocolParamBean paramsBean = new HttpProtocolParamBean(params);
        paramsBean.setVersion(HttpVersion.HTTP_1_1);
        if (site != null && site.getCharset() != null) {
            paramsBean.setContentCharset(site.getCharset());
        }
        paramsBean.setUseExpectContinue(false);

        DefaultHttpClient httpClient = new DefaultHttpClient(connectionManager, params);
        if (site != null) {
            generateCookie(httpClient, site);
        }
        return httpClient;
    }

    private void generateCookie(DefaultHttpClient httpClient, Site site) {
        CookieStore cookieStore = new BasicCookieStore();
        if (site.getCookies() != null) {
            for (Map.Entry<String, String> cookieEntry : site.getCookies().entrySet()) {
                BasicClientCookie cookie = new BasicClientCookie(cookieEntry.getKey(), cookieEntry.getValue());
                cookie.setDomain(site.getDomain());
                cookieStore.addCookie(cookie);
            }
        }
        httpClient.setCookieStore(cookieStore);
    }

	public int getPoolSize() {
		return poolSize;
	}

	public void setPoolSize(int poolSize) {
		this.poolSize = poolSize;
	}

}
