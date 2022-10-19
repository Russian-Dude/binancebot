package com.rdude.binancebot.service;

import com.rdude.binancebot.pojo.SymbolAveragePrice;
import com.rdude.binancebot.pojo.SymbolPrice;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class BinanceApiCaller {

    private final static String PING_REQUEST = "/api/v3/ping";

    private final static String AVERAGE_PRICE_REQUEST = "/api/v3/avgPrice?symbol=";

    private final static String PRICE_REQUEST = "/api/v3/ticker/price?symbol=";

    @Autowired
    @Qualifier("binanceWebClient")
    private WebClient webClient;


    public void ping() {
        webClient
                .method(HttpMethod.GET)
                .uri(PING_REQUEST)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(String.class))
                .subscribe(s -> log.info("Ping to binance. Response body: " + s));
    }

    public Mono<SymbolAveragePrice> averagePrice(String currency1, String currency2) {
        return averagePrice(currency1 + currency2);
    }

    public Mono<SymbolAveragePrice> averagePrice(String symbol) {
        return webClient
                .method(HttpMethod.GET)
                .uri(AVERAGE_PRICE_REQUEST + symbol)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(SymbolAveragePrice.class));
    }

    public Mono<SymbolPrice> price(String currency1, String currency2) {
        return price(currency1 + currency2);
    }

    public Mono<SymbolPrice> price(String symbol) {
        return webClient
                .method(HttpMethod.GET)
                .uri(PRICE_REQUEST + symbol)
                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(SymbolPrice.class));
    }


}
