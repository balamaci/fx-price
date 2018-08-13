package com.balamaci.fxprice.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;

import java.util.concurrent.CountDownLatch;

public class SimpleLogSubscriber<T> extends BaseSubscriber<T> {

    private static final Logger log = LoggerFactory.getLogger(SimpleLogSubscriber.class);

    private CountDownLatch latch;
    private final int subscriberId;



    public SimpleLogSubscriber(CountDownLatch latch, int subscriberId) {
        this.latch = latch;
        this.subscriberId = subscriberId;
    }


//    @Override
//    protected void hookOnSubscribe(Subscription subscription) {
//        subscription.request(1);
//    }

    @Override
    protected void hookOnNext(T value) {
        log.info("Subscriber {} received onNext:{}", subscriberId, value);

    }

    @Override
    protected void hookOnComplete() {
        latch.countDown();
    }
}
