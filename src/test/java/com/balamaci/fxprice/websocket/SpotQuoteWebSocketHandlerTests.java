package com.balamaci.fxprice.websocket;

import org.assertj.core.util.Maps;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import reactor.core.publisher.MonoProcessor;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpotQuoteWebSocketHandlerTests {

	private static final Logger log = LoggerFactory.getLogger(SpotQuoteWebSocketHandlerTests.class);

	private static final Map<Integer, Statistic> statisticsMap = new ConcurrentHashMap<>();

	@LocalServerPort
	private String port;

	@Test
	public void spotPrices() throws Exception {
		int count = 50;

		int noRequests = 500;
		CountDownLatch countDownLatch = new CountDownLatch(noRequests);

	    for(int i=1; i <= noRequests; i++) {
            WebSocketClient client = new ReactorNettyWebSocketClient();
            final int subscriberID = i;
            client.execute(getUrl("/websocket/spot"),
                    session -> {
                        session.receive()
                                .map(WebSocketMessage::getPayloadAsText)
                                .delayElements(Duration.ofMillis(subscriberID % 2 == 0 ? 1000 : 0))
								.take(count)
                                .log("com.balamaci.spot-client")
                                .subscribe(new SimpleLogSubscriber<String>(countDownLatch, subscriberID, statisticsMap));
                        MonoProcessor<Void> completionMono = MonoProcessor.create();
                        return completionMono;
                    }).subscribe();
        }

		countDownLatch.await();

	    showStatistics();
	}

	private void showStatistics() {
		Map<Integer, Integer> quotesReceivedAndSubscribers = new HashMap<>();

		statisticsMap.forEach((key, value) -> {
			Integer quotes = value.getQuotesReceived();

			Integer subscribers = quotesReceivedAndSubscribers.getOrDefault(quotes, 0);
			quotesReceivedAndSubscribers.put(quotes, subscribers + 1);
		});

		quotesReceivedAndSubscribers.forEach((key, val) -> log.info("{} quote - received by {} subscribers", key, val));
	}


	protected URI getUrl(String path) throws URISyntaxException {
		return new URI("ws://localhost:" + this.port + path);
	}


}
