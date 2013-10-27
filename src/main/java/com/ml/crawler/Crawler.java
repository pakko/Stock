package com.ml.crawler;

public interface Crawler {

	ResultItems crawl(Site site);

    void setThread(int threadNum);
}
