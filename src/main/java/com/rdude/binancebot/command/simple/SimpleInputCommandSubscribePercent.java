package com.rdude.binancebot.command.simple;

import com.rdude.binancebot.dto.Symbol;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.*;
import com.rdude.binancebot.state.ChatState;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
public class SimpleInputCommandSubscribePercent extends SimpleInputCommand {

    private final MessageSender messageSender;

    private final BotUserStateService botUserStateService;

    private final SymbolSubscriptionService symbolSubscriptionService;

    private final LastEnteredSymbolService lastEnteredSymbolService;

    private final BinanceApiCaller binanceApiCaller;

    public SimpleInputCommandSubscribePercent(BotUserService botUserService, MessageSender messageSender, MessageSender messageSender1, BotUserStateService botUserStateService, SymbolSubscriptionService symbolSubscriptionService, LastEnteredSymbolService lastEnteredSymbolService, BinanceApiCaller binanceApiCaller) {
        super(botUserService, messageSender);
        this.messageSender = messageSender1;
        this.botUserStateService = botUserStateService;
        this.symbolSubscriptionService = symbolSubscriptionService;
        this.lastEnteredSymbolService = lastEnteredSymbolService;
        this.binanceApiCaller = binanceApiCaller;
    }

    @Override
    public ChatState requiredState() {
        return ChatState.ENTER_CHANGE_PERCENT_SUBSCRIBE;
    }

    @Override
    @Transactional
    public Mono<?> execute(BotUser user, long chatId, String text) {
        text = text.replaceAll("\\s", "");
        if (!text.matches("^\\d+(\\.\\d+)*%?$")) {
            return messageSender.send(user, ReplyMessage.WRONG_PERCENT);
        }
        text = text.replaceAll("%", "");
        String finalText = text;
        var percent = new BigDecimal(finalText);

        return Mono.fromFuture(CompletableFuture
                        .supplyAsync(() -> lastEnteredSymbolService.findByUser(user)))
                .flatMap(symbol -> binanceApiCaller.price(symbol.getSymbolString())
                        .map(price -> new SymbolAndPrice(symbol, price.getPrice())))
                .map(symbolAndPrice -> {
                    symbolSubscriptionService.save(new SymbolSubscription(
                            symbolAndPrice.symbol.getCurrency1(),
                            symbolAndPrice.symbol.getCurrency2(),
                            user,
                            percent,
                            symbolAndPrice.price
                            ));
                    return symbolAndPrice;
                })
                .map(symbolAndPrice -> {
                    BotUserState botUserState = user.getBotUserState();
                    botUserState.setChatState(ChatState.MAIN_MENU);
                    botUserStateService.save(botUserState);
                    return symbolAndPrice;
                })
                .flatMap(symbolAndPrice -> messageSender.sendFormatted(
                        user,
                        ReplyMessage.SUBSCRIBED,
                        symbolAndPrice.symbol.getCurrency1(),
                        symbolAndPrice.symbol.getCurrency2(),
                        percent
                        ));
    }

    @Data
    private static class SymbolAndPrice {
        private final Symbol symbol;
        private final BigDecimal price;
    }
}
