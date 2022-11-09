package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageEditor;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.service.SymbolSubscriptionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import reactor.core.publisher.Mono;

@Component
public class UnsubscribeCallbackCommand extends CallbackCommand {

    private final MessageEditor messageEditor;

    private final SymbolSubscriptionService symbolSubscriptionService;

    public UnsubscribeCallbackCommand(BotUserService botUserService,
                                      MessageSender messageSender,
                                      MessageEditor messageEditor,
                                      SymbolSubscriptionService symbolSubscriptionService) {
        super(botUserService, messageSender);
        this.messageEditor = messageEditor;
        this.symbolSubscriptionService = symbolSubscriptionService;
    }


    @Override
    public String getCallbackData() {
        return "unsubscribe";
    }

    @Override
    protected Mono<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery) {
        return Mono.fromRunnable(() -> {
                    String text = callbackQuery.getMessage().getText();
                    var symbolsText = text.substring(0, text.indexOf("(")).replaceAll("\\s", "");
                    String[] currencies = symbolsText.split("-");
                    symbolSubscriptionService.delete(user, currencies[0], currencies[1]);
                })
                .then(messageEditor.delete(callbackQuery.getMessage().getMessageId(), user));
    }
}
