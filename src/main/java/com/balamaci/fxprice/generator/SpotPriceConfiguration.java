package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Price;
import com.balamaci.fxprice.entity.Side;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpotPriceConfiguration {

    private Random rand = new Random();

    @Bean
    public Flux<Price> generateSpotPrices() {
        return Flux.<Price>create(emitter -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(() -> Arrays.stream(CurrencyPair.values())
                    .forEach(currencyPair -> {
                        if(shouldUpdateCurrencyRandomFactor()) {
                            BigDecimal priceVal = new BigDecimal(rand.nextInt(100) % 100);
                            Price price = new Price(currencyPair, randomSide(), priceVal);

                            emitter.next(price);
                        }
                    }), 0, 500, TimeUnit.MILLISECONDS);
        }).share();
    }


    private boolean shouldUpdateCurrencyRandomFactor() {
        return rand.nextInt(10) > 2;
    }

    private Side randomSide() {
        return rand.nextInt(2) == 0 ? Side.BUY: Side.SELL;
    }

}
