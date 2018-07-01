package com.balamaci.fxprice.generator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.TopicProcessor;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
public class SpotPriceConfiguration {

    @Bean
    public Flux<String> generateSpotPrices() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        TopicProcessor<String> ticker = TopicProcessor.<String>builder().bufferSize(32).build();
        scheduler.scheduleAtFixedRate(() -> {
            String val = "USD: " + System.currentTimeMillis() % 1000;
            ticker.onNext(val);
        }, 0, 250, TimeUnit.MILLISECONDS);

        return ticker;
    }

}
