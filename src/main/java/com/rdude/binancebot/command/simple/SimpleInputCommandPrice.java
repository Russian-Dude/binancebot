package com.rdude.binancebot.command.simple;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.BinanceApiCaller;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;

@Component
public class SimpleInputCommandPrice extends SimpleInputCommand {

    private final BinanceApiCaller binanceApiCaller;

    private final BotUserStateService botUserStateService;

    public SimpleInputCommandPrice(BotUserService botUserService, MessageSender messageSender, BinanceApiCaller binanceApiCaller, BotUserStateService botUserStateService) {
        super(botUserService, messageSender);
        this.binanceApiCaller = binanceApiCaller;
        this.botUserStateService = botUserStateService;
    }

    @Override
    public ChatState requiredState() {
        return ChatState.ENTER_SYMBOL_PAIR_GET_PRICE;
    }

    @Override
    public CompletableFuture<?> execute(BotUser user, long chatId, String text) {
        String[] args = getCurrenciesFromString(text);
        BotUserState botUserState = user.getBotUserState();
        botUserState.setChatState(ChatState.MAIN_MENU);
        botUserStateService.save(botUserState);
        // in format "BTCUSDT"
        if (args.length == 1) {
            return binanceApiCaller.price(args[0])
                    .map(price -> {
                        BigDecimal p = price.getPrice();
                        if (p == null) return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                        else return messageSender.sendFormatted(
                                user,
                                ReplyMessage.PRICE_X,
                                args[0],
                                price.getPrice());
                    })
                    .onErrorResume(e -> Mono.just(CompletableFuture.failedFuture(new RuntimeException("SimpleInputCommandPrice error while calling BinanceApiCaller.price() with symbol " + args[0] + ". Exception message: " + e.getMessage()))))
                    .block();
        }
        // in format "BTC, USDT"
        else if (args.length == 2) {
            String currency1 = args[0];
            String currency2 = args[1];
            return binanceApiCaller.price(currency1, currency2)
                    .map(price -> {
                        BigDecimal p = price.getPrice();
                        if (p == null) return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                        else return messageSender.sendFormatted(
                                user,
                                ReplyMessage.PRICE_X_TO_Y,
                                currency1,
                                currency2,
                                price.getPrice()
                        );
                    })
                    .onErrorResume(e -> Mono.just(CompletableFuture.failedFuture(new RuntimeException("SimpleInputCommandPrice error while calling BinanceApiCaller.price() with currencies " + currency1 + ", " + currency2 + ". Exception message: " + e.getMessage()))))
                    .block();
        } else {
            return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
        }
    }
}
