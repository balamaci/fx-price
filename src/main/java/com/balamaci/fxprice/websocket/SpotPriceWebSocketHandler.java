package com.balamaci.fxprice.websocket;

import com.balamaci.fxprice.entity.Price;
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

public class SpotPriceWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.
            getLogger(SpotPriceWebSocketHandler.class);

    private Flux<Price> spotPricesStream;

    private ObjectWriter jsonWriter = new ObjectMapper().writer();

    public SpotPriceWebSocketHandler(Flux<Price> spotPricesStream) {
        this.spotPricesStream = spotPricesStream;
    }

    @Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> wsMessage = spotPricesStream
                .onBackpressureDrop(val -> log.info("******DROPPED " + val))
                .map((price) -> {
                    try {
                        return jsonWriter.writeValueAsString(price);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .log("com.balamaci.ws-handler")
                .map(session::textMessage);

	    return session.send(wsMessage);
	}
}
