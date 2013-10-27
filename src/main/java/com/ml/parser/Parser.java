package com.ml.parser;

import java.util.List;

public interface Parser<T> {
	List<T> parse(String stockCode, String content);
}
