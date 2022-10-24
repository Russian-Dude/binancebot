package com.rdude.binancebot.api;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

    public CompletableFuture<Message> execute(SendMessage sendMessage) {
        try {
            return binanceBot.executeAsync(sendMessage);
        }
        catch (TelegramApiException e) {
            return CompletableFuture.failedFuture(new RuntimeException("Can not execute SendMessage " + sendMessage));
        }
    }

    public <T extends Serializable> CompletableFuture<T> execute(BotApiMethod<T> method) {
        try {
            return binanceBot.executeAsync(method);
        }
        catch (TelegramApiException e) {
            return CompletableFuture.failedFuture(new RuntimeException("Can not execute BotApiMethod " + method));
        }
    }

    public CompletableFuture<Void> execute(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, binanceBot.getExecutor());
    }

    public <T> CompletableFuture<T> execute(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, binanceBot.getExecutor());
    }

}
