package com.balamaci.fxprice.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CurrencyPair {

    EUR_USD("EUR", "USD"),
    EUR_CHF("EUR", "CHF"),
    EUR_GBP("EUR", "GBP"),
    EUR_JPY("EUR", "JPY"),
    EUR_SGD("EUR", "SGD"),
    USD_SGD("USD", "SGD"),
    GBP_USD("GBP", "USD");

    private String first;
    private String second;

    CurrencyPair(String first, String second) {
        this.first = first;
        this.second = second;
    }

    @JsonValue
    public String getPair() {
        return first + "/" + second;
    }

    public String getPairNoSep() {
        return first + second;
    }

    public String getFirst() {
        return first;
    }

    public String getSecond() {
        return second;
    }
}
