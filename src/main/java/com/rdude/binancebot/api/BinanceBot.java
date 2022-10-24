package com.rdude.binancebot.api;

import com.rdude.binancebot.config.BotConfig;
import com.rdude.binancebot.handlers.CallbackQueryHandler;
import com.rdude.binancebot.handlers.ExceptionHandler;
import com.rdude.binancebot.handlers.MessageHandler;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import javax.annotation.PostConstruct;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

@Component
@Getter
public class BinanceBot extends SpringWebhookBot {

    private final String botUsername;

    private final String botToken;

    private final String botPath;

    private final MessageHandler messageHandler;

    private final CallbackQueryHandler callbackQueryHandler;

    private final ExceptionHandler exceptionHandler;

    private final ApplicationContext context;


    @Autowired
    public BinanceBot(BotConfig botConfig,
                      SetWebhook setWebhook,
                      MessageHandler messageHandler,
                      CallbackQueryHandler callbackQueryHandler,
                      ExceptionHandler exceptionHandler,
                      ApplicationContext context) {
        super(setWebhook);
        this.botUsername = botConfig.getBotName();
        this.botToken = botConfig.getBotToken();
        this.botPath = botConfig.getWebhookPath();
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
        this.exceptionHandler = exceptionHandler;
        this.context = context;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        CompletableFuture<?> result = null;

        // callback query
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChat().isUserChat()) {
            result = callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
        }

        // text message
        else if (update.hasMessage() && update.getMessage().isUserMessage() && update.getMessage().hasText()) {
            result = messageHandler.processMessage(update.getMessage());
        }

        if (result != null) {
            result.exceptionally(throwable -> {
                        exceptionHandler.handle(throwable, update.getCallbackQuery().getMessage().getChatId());
                        return null;
                    })
                    .join();
        }
        return null;
    }

    ExecutorService getExecutor() {
        return exe;
    }

    @PostConstruct
    private void init() {
        context.getBean(BotMethodsExecutor.class).setBinanceBot(this);
    }

}
