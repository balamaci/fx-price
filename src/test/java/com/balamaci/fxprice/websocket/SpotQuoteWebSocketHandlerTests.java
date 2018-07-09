package com.balamaci.fxprice.websocket;

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
import java.util.concurrent.CountDownLatch;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SpotQuoteWebSocketHandlerTests {

	private static final Logger log = LoggerFactory.getLogger(SpotQuoteWebSocketHandlerTests.class);

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
                                .take(count)
                                .log("com.balamaci.spot-client")
                                .subscribe(new SimpleLogSubscriber<String>(countDownLatch, subscriberID));
                        MonoProcessor<Void> completionMono = MonoProcessor.create();
                        return completionMono;
                    }).subscribe();
        }

		countDownLatch.await();
	}



	protected URI getUrl(String path) throws URISyntaxException {
		return new URI("ws://localhost:" + this.port + path);
	}
}
