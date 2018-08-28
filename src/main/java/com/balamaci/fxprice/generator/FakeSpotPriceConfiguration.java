package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.Quote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Configuration
@ConditionalOnProperty(value = "generator", havingValue = "fake", matchIfMissing = true)
public class FakeSpotPriceConfiguration {

    private static final Logger log = LoggerFactory.getLogger(FakeSpotPriceConfiguration.class);

    private RandomQuoteGenerator randomQuoteGenerator = new RandomQuoteGenerator();

    @Bean
    public Flux<Quote> generateSpotPrices() {
        return Flux.push(this::generateRandomPrices, FluxSink.OverflowStrategy.LATEST)
                .share();
    }

    private void generateRandomPrices(FluxSink<Quote> sink) {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        scheduler.scheduleAtFixedRate(() -> {
                    if (!sink.isCancelled()) {
                        Quote quote = randomQuoteGenerator.generate();

                        log.info("Pushing {}", quote);
                        sink.next(quote);
                    }
                }
                , 0, 500, TimeUnit.MILLISECONDS);
    }

}
