package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Price;
import com.balamaci.fxprice.entity.Side;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

import java.math.BigDecimal;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpotPriceConfiguration {

    @Bean
    public Flux<Price> generateSpotPrices() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        TopicProcessor<Price> ticker = TopicProcessor.<Price>builder()
                .bufferSize(4)
                .build();

        scheduler.scheduleAtFixedRate(() -> {
            BigDecimal priceVal = new BigDecimal(System.currentTimeMillis() % 1000);
            Price price = new Price(CurrencyPair.EUR_USD, Side.BID, priceVal);

            ticker.onNext(price);
        }, 0, 250, TimeUnit.MILLISECONDS);

        return ticker;
    }

}
