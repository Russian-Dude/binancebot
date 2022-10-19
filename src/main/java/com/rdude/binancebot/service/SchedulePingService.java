package com.rdude.binancebot.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Getter
@Setter
@Slf4j
public class SchedulePingService {

    @Value("${bot.ping-address}")
    private String url;

    @Scheduled(fixedRateString = "${bot.ping-period}")
    public void ping() {
        try {
            URL pingUrl = new URL(getUrl());
            HttpURLConnection urlConnection = (HttpURLConnection) pingUrl.openConnection();
            urlConnection.connect();
            log.info("Scheduled ping {}. Response code: {}", pingUrl.getHost(), urlConnection.getResponseCode());
            urlConnection.disconnect();
        } catch (IOException e) {
            log.error("Scheduled ping error. {}", e.getMessage());
        }
    }

}
