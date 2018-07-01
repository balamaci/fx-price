package com.balamaci.fxprice.entity;

import java.math.BigDecimal;

public class Price {

    private final CurrencyPair currencyPair;

    private final Side side;

    private final BigDecimal price;

    public Price(CurrencyPair currencyPair, Side side, BigDecimal price) {
        this.currencyPair = currencyPair;
        this.side = side;
        this.price = price;
    }

    public CurrencyPair getCurrencyPair() {
        return currencyPair;
    }

    public Side getSide() {
        return side;
    }

    public BigDecimal getPrice() {
        return price;
    }
}
