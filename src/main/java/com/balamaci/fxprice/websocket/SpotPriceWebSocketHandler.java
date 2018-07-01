package com.balamaci.fxprice.websocket;

import com.balamaci.fxprice.entity.Price;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SpotPriceWebSocketHandler implements WebSocketHandler {

    private Flux<Price> spotPricesStream;

    private ObjectWriter jsonWriter = new ObjectMapper().writer();

    public SpotPriceWebSocketHandler(Flux<Price> spotPricesStream) {
        this.spotPricesStream = spotPricesStream;
    }

    @Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> wsMessage = spotPricesStream
                .map((price) -> {
                    try {
                        return jsonWriter.writeValueAsString(price);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                })
                .log("com.balamaci.spotprice")
                .map(session::textMessage);

	    return session.send(wsMessage);
	}
}
