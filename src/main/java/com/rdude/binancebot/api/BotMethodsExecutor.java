package com.rdude.binancebot.api;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import reactor.core.publisher.Mono;

import java.io.Serializable;

@Component
public class BotMethodsExecutor {

    private BinanceBot binanceBot;

    private final ApplicationContext context;

    public BotMethodsExecutor(ApplicationContext context) {
        this.context = context;
    }

    public void setBinanceBot(BinanceBot binanceBot) {
        this.binanceBot = binanceBot;
    }

    public Mono<Message> execute(SendMessage sendMessage) {
        try {
            return Mono.fromFuture(binanceBot.executeAsync(sendMessage));
        }
        catch (TelegramApiException e) {
            return Mono.error(new RuntimeException("Can not execute SendMessage " + sendMessage));
        }
    }

    public <T extends Serializable> Mono<T> execute(BotApiMethod<T> method) {
        try {
            return Mono.fromFuture(binanceBot.executeAsync(method));
        }
        catch (TelegramApiException e) {
            return Mono.error(new RuntimeException("Can not execute BotApiMethod " + method));
        }
    }

}
