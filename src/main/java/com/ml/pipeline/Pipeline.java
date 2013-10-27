package com.ml.pipeline;

import java.util.List;

public interface Pipeline<T> {

    public void process(List<T> datas);
}
