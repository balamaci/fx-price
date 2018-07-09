package com.balamaci.fxprice.entity;

import java.math.BigDecimal;

public class Quote {

    private final CurrencyPair currencyPair;

    private final Side side;

    private final BigDecimal price;

    public Quote(CurrencyPair currencyPair, Side side, BigDecimal price) {
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

    @Override
    public String toString() {
        return "Quote{" +
                "currencyPair=" + currencyPair +
                ", side=" + side +
                ", price=" + price +
                '}';
    }
}
