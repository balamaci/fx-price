package com.balamaci.fxprice.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyPair {

    EUR_USD("EUR/USD"),
    EUR_CHF("EUR/CHF");

    private String pair;

    CurrencyPair(String pair) {
        this.pair = pair;
    }

    @JsonValue
    public String getPair() {
        return pair;
    }
}
