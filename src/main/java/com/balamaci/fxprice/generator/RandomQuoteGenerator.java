package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Quote;
import com.balamaci.fxprice.entity.Side;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author sbalamaci
 */
public class RandomQuoteGenerator {

    private Random rand = new Random();

    private static final List<Pair<CurrencyPair, Double>> pairs = new ArrayList<>();

    static {
        pairs.add(new Pair<>(CurrencyPair.EUR_USD, 0.2));
        pairs.add(new Pair<>(CurrencyPair.EUR_SGD, 0.2));
        pairs.add(new Pair<>(CurrencyPair.EUR_CHF, 0.2));
        pairs.add(new Pair<>(CurrencyPair.EUR_GBP, 0.2));
        pairs.add(new Pair<>(CurrencyPair.EUR_JPY, 0.1));
        pairs.add(new Pair<>(CurrencyPair.GBP_USD, 0.1));
    }

    private EnumeratedDistribution<CurrencyPair> randCurrencyDistribution =
            new EnumeratedDistribution<>(pairs);

    public Quote generate() {
        return new Quote(randomCurrencyPair(), randomSide(), randomPrice());
    }

    private CurrencyPair randomCurrencyPair() {
        return randCurrencyDistribution.sample();
    }

    private BigDecimal randomPrice() {
        return new BigDecimal(rand.nextInt(100) % 100);
    }

    private Side randomSide() {
        return rand.nextInt(2) == 0 ? Side.BUY: Side.SELL;
    }


}
