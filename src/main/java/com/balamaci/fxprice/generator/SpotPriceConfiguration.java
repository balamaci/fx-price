package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Price;
import com.balamaci.fxprice.entity.Side;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;
import reactor.util.concurrent.WaitStrategy;

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
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        TopicProcessor<Price> ticker = TopicProcessor.<Price>builder()
                .name("spot-price-topic")
                .waitStrategy(WaitStrategy.sleeping())
//                .executor(Executors.newFixedThreadPool(20))
                .bufferSize(16)
                .build();


        scheduler.scheduleAtFixedRate(() -> Arrays.stream(CurrencyPair.values())
                .forEach(currencyPair -> {
                    if(! ticker.hasDownstreams()) {
                        return;
                    }

                    if(shouldUpdateCurrencyRandomFactor()) {
                        BigDecimal priceVal = new BigDecimal(rand.nextInt(100) % 100);
                        Price price = new Price(currencyPair, randomSide(), priceVal);

                        ticker.onNext(price);
                    }
                }), 0, 500, TimeUnit.MILLISECONDS);

        return ticker;
    }

    private boolean shouldUpdateCurrencyRandomFactor() {
        return rand.nextInt(10) > 2;
    }

    private Side randomSide() {
        return rand.nextInt(2) == 0 ? Side.BUY: Side.SELL;
    }

}
