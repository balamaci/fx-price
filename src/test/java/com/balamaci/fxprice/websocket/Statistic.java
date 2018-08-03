package com.balamaci.fxprice.websocket;

class Statistic {

    private Integer quotesReceived;

    public Statistic(Integer quotesReceived) {
        this.quotesReceived = quotesReceived;
    }

    public Integer getQuotesReceived() {
        return quotesReceived;
    }

}
