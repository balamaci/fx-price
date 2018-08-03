package com.balamaci.fxprice.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;

import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class SimpleLogSubscriber<T> extends BaseSubscriber<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleLogSubscriber.class);

    private CountDownLatch latch;
    private final int subscriberId;

    private Map<Integer, Statistic> statisticsMap;

    private int quotesReceived;

    public SimpleLogSubscriber(CountDownLatch latch, int subscriberId,
                               Map<Integer, Statistic> statisticsMap) {
        this.latch = latch;
        this.subscriberId = subscriberId;
        this.statisticsMap = statisticsMap;
    }


//    @Override
//    protected void hookOnSubscribe(Subscription subscription) {
//        subscription.request(1);
//    }

    @Override
    protected void hookOnNext(T value) {
        log.info("Subscriber {} received onNext:{}", subscriberId, value);
        quotesReceived ++;
    }

    @Override
    protected void hookOnComplete() {
        statisticsMap.put(subscriberId, new Statistic(quotesReceived));
        latch.countDown();
    }
}
