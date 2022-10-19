package com.rdude.binancebot.api;

import com.rdude.binancebot.config.BotConfig;
import com.rdude.binancebot.handlers.CallbackQueryHandler;
import com.rdude.binancebot.handlers.MessageHandler;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.starter.SpringWebhookBot;

@Component
@Getter
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
    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChat().isUserChat()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            return callbackQueryHandler.processCallbackQuery(callbackQuery);
        } else if (update.hasMessage() && update.getMessage().isUserMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            return messageHandler.processMessage(message);
        }
        return null;
    }


}
