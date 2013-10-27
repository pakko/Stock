package com.ml.strategy;
public class Client {

    public static void main(String[] args) {
        //选择并创建需要使用的策略对象
        Strategy strategy = new StrategyA();
        //创建环境
        Context context = new Context(strategy);
        //计算价格
        context.contextInterface();
    }

}