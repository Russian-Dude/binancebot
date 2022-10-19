package com.rdude.binancebot.config;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotConfig {

    @Value("${bot.webhook-path}")
    String webhookPath;

    @Value("${bot.name}")
    String botName;

    @Value("${bot.token}")
    String botToken;

}
