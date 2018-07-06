package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Price;
import com.balamaci.fxprice.entity.Side;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(value = "generator", havingValue = "fake", matchIfMissing = true)
public class FakeSpotPriceConfiguration {

    private Random rand = new Random();



    @Bean
    public Flux<Price> generateSpotPrices() {
        return Flux.<Price>create(sink -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(() -> Arrays.stream(CurrencyPair.values())
                    .forEach(currencyPair -> {
                        if(! sink.isCancelled()) {
                            if (shouldUpdateCurrencyRandomFactor()) {
                                BigDecimal priceVal = randomPrice();
                                Price price = new Price(currencyPair, randomSide(), priceVal);

                                sink.next(price);
                            }
                        }
                    }), 0, 200, TimeUnit.MILLISECONDS);
        }, FluxSink.OverflowStrategy.DROP)
                .share();
    }


    private boolean shouldUpdateCurrencyRandomFactor() {
        return rand.nextInt(10) > 2;
    }

    private BigDecimal randomPrice() {
        return new BigDecimal(rand.nextInt(100) % 100);
    }

    private Side randomSide() {
        return rand.nextInt(2) == 0 ? Side.BUY: Side.SELL;
    }

}
