package com.rdude.binancebot.command.simple;

import com.rdude.binancebot.dto.Symbol;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.*;
import com.rdude.binancebot.state.ChatState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
public class SimpleInputCommandSubscribeSymbolEnterSymbol extends SimpleInputCommand {

    private final LastEnteredSymbolService lastEnteredSymbolService;

    private final BotUserStateService botUserStateService;

    private final BinanceApiCaller binanceApiCaller;

    public SimpleInputCommandSubscribeSymbolEnterSymbol(BotUserService botUserService, MessageSender messageSender, LastEnteredSymbolService lastEnteredSymbolService, BotUserStateService botUserStateService, BinanceApiCaller binanceApiCaller) {
        super(botUserService, messageSender);
        this.lastEnteredSymbolService = lastEnteredSymbolService;
        this.botUserStateService = botUserStateService;
        this.binanceApiCaller = binanceApiCaller;
    }

    @Override
    public ChatState requiredState() {
        return ChatState.ENTER_SYMBOL_PAIR_SUBSCRIBE;
    }

    @Override
    public Mono<?> execute(BotUser user, long chatId, String text) {
        String[] args = getCurrenciesFromString(text);
        BotUserState botUserState = user.getBotUserState();
        // in format "BTC, USDT"
        if (args.length == 2) {
            String currency1 = args[0];
            String currency2 = args[1];
            return binanceApiCaller.price(currency1, currency2)
                    .flatMap(price -> {
                        BigDecimal p = price.getPrice();
                        if (p == null) return messageSender
                                .send(user, ReplyMessage.WRONG_SYMBOLS_PAIR)
                                .then(messageSender.send(user, ReplyMessage.ENTER_SYMBOL_PAIR));
                        else {
                            lastEnteredSymbolService.save(user, new Symbol(currency1, currency2));
                            botUserState.setChatState(ChatState.ENTER_CHANGE_PERCENT_SUBSCRIBE);
                            botUserStateService.save(botUserState);
                            return messageSender.send(user, ReplyMessage.ENTER_PERCENT);
                        }
                    });
        } else {
            return messageSender
                    .send(user, ReplyMessage.WRONG_SYMBOLS_PAIR)
                    .then(messageSender.send(user, ReplyMessage.ENTER_SYMBOL_PAIR));
        }
    }
}
