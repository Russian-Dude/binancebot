package com.rdude.binancebot.api;

import com.rdude.binancebot.config.BotConfig;
import com.rdude.binancebot.handlers.CallbackQueryHandler;
import com.rdude.binancebot.handlers.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.io.Serializable;

@Component
@Getter
@Slf4j
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BinanceBot extends SpringWebhookBot {

    String botUsername;

    String botToken;

    String botPath;

    MessageHandler messageHandler;

    CallbackQueryHandler callbackQueryHandler;


    @Autowired
    public BinanceBot(BotConfig botConfig,
                      SetWebhook setWebhook,
                      MessageHandler messageHandler,
                      CallbackQueryHandler callbackQueryHandler) {
        super(setWebhook);
        this.botUsername = botConfig.getBotName();
        this.botToken = botConfig.getBotToken();
        this.botPath = botConfig.getWebhookPath();
        this.messageHandler = messageHandler;
        this.callbackQueryHandler = callbackQueryHandler;
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChat().isUserChat()) {
            executeMethodsChain(callbackQueryHandler.processCallbackQuery(update.getCallbackQuery()));
        }
        else if (update.hasMessage() && update.getMessage().isUserMessage() && update.getMessage().hasText()) {
            executeMethodsChain(messageHandler.processMessage(update.getMessage()));
        }
        return null;
    }

    private void executeMethodsChain(BotMethodsChainEntry<?> anyEntry) {
        Serializable currentResult = null;
        BotMethodsChainEntry<?> currentEntry = anyEntry.getRootEntry();
        while (currentEntry != null) {
            currentResult = currentEntry.execute(currentResult, this::executeWithCatch);
            currentEntry = currentEntry.getNextEntry();
        }
    }

    private <T extends Serializable> T executeWithCatch(BotApiMethod<T> method) {
        try {
            return execute(method);
        }
        catch (TelegramApiException e) {
            log.error("Error while executing bot methods chain.", e);
            return null;
        }
    }

}
