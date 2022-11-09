package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.reply.menu.UnsubscribeInlineMenu;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.service.SymbolSubscriptionService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import reactor.core.publisher.Mono;

@Component
public class MySubscriptionsCallbackCommand extends CallbackCommand {

    private final MessageSender messageSender;

    private final SymbolSubscriptionService symbolSubscriptionService;

    private final UnsubscribeInlineMenu unsubscribeInlineMenu;

    public MySubscriptionsCallbackCommand(BotUserService botUserService,
                                          MessageSender messageSender,
                                          MessageSender messageSender1,
                                          SymbolSubscriptionService symbolSubscriptionService,
                                          UnsubscribeInlineMenu unsubscribeInlineMenu) {
        super(botUserService, messageSender);
        this.messageSender = messageSender1;
        this.symbolSubscriptionService = symbolSubscriptionService;
        this.unsubscribeInlineMenu = unsubscribeInlineMenu;
    }


    @Override
    public String getCallbackData() {
        return "my_subscriptions";
    }

    @Override
    protected Mono<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery) {
        return Mono.fromRunnable(() -> symbolSubscriptionService.findByUser(user)
                .forEach(subscription -> messageSender.sendFormatted(
                        user,
                        ReplyMessage.SUBSCRIPTION,
                        unsubscribeInlineMenu.getMarkup(user),
                        subscription.getCurrency1(),
                        subscription.getCurrency2(),
                        subscription.getRequiredPercentForNotification())));
    }
}
