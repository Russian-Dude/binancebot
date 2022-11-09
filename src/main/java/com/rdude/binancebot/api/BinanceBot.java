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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
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
        Mono<?> result = null;

        // callback query
        if (update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChat().isUserChat()) {
            result = callbackQueryHandler.processCallbackQuery(update.getCallbackQuery());
        }

        // text message
        else if (update.hasMessage() && update.getMessage().isUserMessage() && update.getMessage().hasText()) {
            result = messageHandler.processMessage(update.getMessage());
        }

        if (result != null) {
            result.doOnError(e -> exceptionHandler.handle(e, update.getCallbackQuery().getMessage().getChatId()))
                    .onErrorComplete()
                    .subscribe();
        }
        return null;
    }

    public void executeInChat(long chatId, Mono<?> mono) {
        mono.doOnError(e -> exceptionHandler.handle(e, chatId))
                .onErrorComplete()
                .subscribe();
    }

    public void executeInChat(long chatId, Flux<?> flux) {
        flux.onErrorContinue((e, __) -> exceptionHandler.handle(e, chatId))
                .subscribe();
    }

    public void execute(Mono<?> mono) {
        mono.doOnError(exceptionHandler::handle)
                .onErrorComplete()
                .subscribe();
    }

    public void execute(Flux<?> flux) {
        flux.onErrorContinue((e, __) -> exceptionHandler.handle(e))
                .subscribe();
    }

    ExecutorService getExecutor() {
        return exe;
    }

    @PostConstruct
    private void init() {
        context.getBean(BotMethodsExecutor.class).setBinanceBot(this);
    }

}
