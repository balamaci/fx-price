package com.balamaci.fxprice.generator;

import com.balamaci.fxprice.entity.CurrencyPair;
import com.balamaci.fxprice.entity.Quote;
import com.balamaci.fxprice.entity.Side;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(FakeSpotPriceConfiguration.class);

    @Bean
    public Flux<Quote> generateSpotPrices() {
        return Flux.<Quote>create(sink -> {
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

            scheduler.scheduleAtFixedRate(() -> Arrays.stream(CurrencyPair.values())
                    .forEach(currencyPair -> {
                        if(! sink.isCancelled()) {
                            if (shouldUpdateCurrencyRandomFactor()) {
                                BigDecimal priceVal = randomPrice();
                                Quote quote = new Quote(currencyPair, randomSide(), priceVal);

                                log.info("Pushing {}", quote);
                                sink.next(quote);
                            }
                        }
                    }), 0, 200, TimeUnit.MILLISECONDS);
        }, FluxSink.OverflowStrategy.LATEST)
                .share();
    }


    private boolean shouldUpdateCurrencyRandomFactor() {
        return rand.nextInt(10) > 2;
    }

    private BigDecimal randomPrice() {
        return new BigDecimal(rand.nextInt(100) % 100);
    }

    /**
     * Randomly generate a BUY or SELL Side for the Quote
     * @return random either BUY or SELL
     */
    private Side randomSide() {
        return rand.nextInt(2) == 0 ? Side.BUY: Side.SELL;
    }

}
