package com.balamaci.fxprice.websocket;

import com.balamaci.fxprice.entity.Quote;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

public class SpotPriceWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(SpotPriceWebSocketHandler.class);

    private AtomicLong subscrId = new AtomicLong(0);

    private Flux<Quote> spotPricesStream;

    private ObjectWriter jsonWriter = new ObjectMapper().writer();

    public SpotPriceWebSocketHandler(Flux<Quote> spotPricesStream) {
        this.spotPricesStream = spotPricesStream;
    }

    @Override
	public Mono<Void> handle(WebSocketSession session) {
        log.info("New web socket connection {}", subscrId.incrementAndGet());

		Flux<WebSocketMessage> wsMessage = spotPricesStream
                .log("com.balamaci.ws-handler")
                .onBackpressureDrop(val -> log.info("******DROPPED " + val))
                .map((price) -> {
                    try {
                        String json = jsonWriter.writeValueAsString(price);
                        log.info("Mapping {}", json);
                        return jsonWriter.writeValueAsString(price);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .map(session::textMessage);

//        if(subscrId.get() % 2 == 0) {
//            wsMessage = wsMessage.delayElements(Duration.ofMillis(1000));
//        }

	    return session.send(wsMessage);
	}
}
