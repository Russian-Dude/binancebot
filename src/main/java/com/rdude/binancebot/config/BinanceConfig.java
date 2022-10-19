package com.rdude.binancebot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

@Configuration
public class BinanceConfig {

    @Bean(name = "binanceWebClient")
    public WebClient getWebClient() {
        return WebClient.builder()
                .baseUrl("https://api.binance.com")
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create().followRedirect(true)
                ))
                .build();
    }

}
