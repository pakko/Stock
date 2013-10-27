package com.ml.crawler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class Site {
	private String url;
	
    private String domain;

    private String userAgent;

    private Map<String, String> cookies = new LinkedHashMap<String, String>();

    private String charset;

    private int sleepTime = 3000;

    private int timeOut = 2000;

    private static final Set<Integer> DEFAULT_STATUS_CODE_SET = new HashSet<Integer>();

    private Set<Integer> acceptStatCode = DEFAULT_STATUS_CODE_SET;

    private Map<String,String> headers = new HashMap<String, String>();

    public static interface HeaderConst {

        public static final String REFERER = "Referer";
    }


    static {
        DEFAULT_STATUS_CODE_SET.add(200);
    }

    /**
     * new a Site
     *
     * @return new site
     */
    public static Site getInstance() {
        return new Site();
    }

    /**
     * Add a cookie with domain {@link #getDomain()}
     *
     * @param name
     * @param value
     * @return this
     */
    public Site addCookie(String name, String value) {
        cookies.put(name, value);
        return this;
    }

    /**
     * set user agent
     *
     * @param userAgent userAgent
     * @return this
     */
    public Site setUserAgent(String userAgent) {
        this.userAgent = userAgent;
        return this;
    }

    /**
     * get cookies
     *
     * @return get cookies
     */
    public Map<String, String> getCookies() {
        return cookies;
    }

    /**
     * get user agent
     *
     * @return user agent
     */
    public String getUserAgent() {
        return userAgent;
    }

    /**
     * get domain
     *
     * @return get domain
     */
    public String getDomain() {
        return domain;
    }

    /**
     * set the domain of site.
     *
     * @param domain
     * @return this
     */
    public Site setDomain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Set charset of page manually.<br>
     * When charset is not set or set to null, it can be auto detected by Http header.
     *
     * @param charset
     * @return this
     */
    public Site setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    /**
     * get charset set manually
     *
     * @return charset
     */
    public String getCharset() {
        return charset;
    }

    public int getTimeOut() {
        return timeOut;
    }

    /**
     * set timeout for downloader in ms
     *
     * @param timeOut
     */
    public Site setTimeOut(int timeOut) {
        this.timeOut = timeOut;
        return this;
    }

    /**
     * Set acceptStatCode.<br>
     * When status code of http response is in acceptStatCodes, it will be processed.<br>
     * {200} by default.<br>
     * It is not necessarily to be set.<br>
     *
     * @param acceptStatCode
     * @return this
     */
    public Site setAcceptStatCode(Set<Integer> acceptStatCode) {
        this.acceptStatCode = acceptStatCode;
        return this;
    }

    /**
     * get acceptStatCode
     *
     * @return acceptStatCode
     */
    public Set<Integer> getAcceptStatCode() {
        return acceptStatCode;
    }


    /**
     * Set the interval between the processing of two pages.<br>
     * Time unit is micro seconds.<br>
     *
     * @param sleepTime
     * @return this
     */
    public Site setSleepTime(int sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    /**
     * Get the interval between the processing of two pages.<br>
     * Time unit is micro seconds.<br>
     *
     * @return the interval between the processing of two pages,
     */
    public int getSleepTime() {
        return sleepTime;
    }


    public Map<String, String> getHeaders() {
        return headers;
    }

    /**
     * Put an Http header for downloader. <br/>
     * Use {@link #addCookie(String, String)} for cookie and {@link #setUserAgent(String)} for user-agent. <br/>
     * @param key key of http header, there are some keys constant in {@link HeaderConst}
     * @param value value of header
     * @return
     */
    public Site addHeader(String key, String value){
        headers.put(key,value);
        return this;
    }

	public String getUrl() {
		return url;
	}

	public Site setUrl(String url) {
		this.url = url;
		return this;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((acceptStatCode == null) ? 0 : acceptStatCode.hashCode());
		result = prime * result + ((charset == null) ? 0 : charset.hashCode());
		result = prime * result + ((cookies == null) ? 0 : cookies.hashCode());
		result = prime * result + ((domain == null) ? 0 : domain.hashCode());
		result = prime * result + ((headers == null) ? 0 : headers.hashCode());
		result = prime * result + sleepTime;
		result = prime * result + timeOut;
		result = prime * result + ((url == null) ? 0 : url.hashCode());
		result = prime * result
				+ ((userAgent == null) ? 0 : userAgent.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Site other = (Site) obj;
		if (acceptStatCode == null) {
			if (other.acceptStatCode != null)
				return false;
		} else if (!acceptStatCode.equals(other.acceptStatCode))
			return false;
		if (charset == null) {
			if (other.charset != null)
				return false;
		} else if (!charset.equals(other.charset))
			return false;
		if (cookies == null) {
			if (other.cookies != null)
				return false;
		} else if (!cookies.equals(other.cookies))
			return false;
		if (domain == null) {
			if (other.domain != null)
				return false;
		} else if (!domain.equals(other.domain))
			return false;
		if (headers == null) {
			if (other.headers != null)
				return false;
		} else if (!headers.equals(other.headers))
			return false;
		if (sleepTime != other.sleepTime)
			return false;
		if (timeOut != other.timeOut)
			return false;
		if (url == null) {
			if (other.url != null)
				return false;
		} else if (!url.equals(other.url))
			return false;
		if (userAgent == null) {
			if (other.userAgent != null)
				return false;
		} else if (!userAgent.equals(other.userAgent))
			return false;
		return true;
	}

    
}
