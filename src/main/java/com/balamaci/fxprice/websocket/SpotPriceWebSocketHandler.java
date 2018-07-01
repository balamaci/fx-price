package com.balamaci.fxprice.websocket;

import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class SpotPriceWebSocketHandler implements WebSocketHandler {

    private Flux<String> spotPricesStream;

    public SpotPriceWebSocketHandler(Flux<String> spotPricesStream) {
        this.spotPricesStream = spotPricesStream;
    }

    @Override
	public Mono<Void> handle(WebSocketSession session) {
		Flux<WebSocketMessage> wsMessage = spotPricesStream
                .log("com.balamaci.spotprice")
                .map(session::textMessage);

	    return session.send(wsMessage);
	}
}
