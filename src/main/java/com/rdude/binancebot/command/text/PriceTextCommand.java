package com.rdude.binancebot.command.text;

import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.BinanceApiCaller;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Component
@Slf4j
public class PriceTextCommand extends TextCommand {

    private static final String COMMAND_TEXT = "/price";

    private final BinanceApiCaller binanceApiCaller;

    public PriceTextCommand(BotUserService botUserService, MessageSender messageSender, BinanceApiCaller binanceApiCaller, BotUserStateService botUserStateService) {
        super(botUserService, botUserStateService, messageSender);
        this.binanceApiCaller = binanceApiCaller;
    }

    @Override
    public boolean checkString(String text) {
        return text.startsWith(COMMAND_TEXT);
    }

    @Override
    public boolean isRequiresRegistration() {
        return false;
    }

    @Override
    protected BotMethodsChainEntry<?> execute(BotUser user, long chatId, String text) {
        if (text.equals(COMMAND_TEXT)) {
            BotUserState botUserState = user.getBotUserState();
            botUserState.setChatState(ChatState.ENTER_SYMBOL_PAIR_GET_PRICE);
            botUserStateService.save(botUserState);
            return messageSender.send(user, ReplyMessage.ENTER_SYMBOL_PAIR);
        }
        else {
            String[] args = getArgs(COMMAND_TEXT, text);
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
                        .doOnError(e -> log.error("PriceTextCommand error while calling BinanceApiCaller.price() with symbol " + args[0], e))
                        .onErrorResume(__ -> Mono.just(messageSender.sendErrorOccurred(user, chatId)))
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
                        .doOnError(e -> log.error("PriceTextCommand error while calling BinanceApiCaller.price() with currencies " + currency1 + ", " + currency2, e))
                        .onErrorResume(__ -> Mono.just(messageSender.sendErrorOccurred(user, chatId)))
                        .block();
            } else {
                return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
            }
        }
    }
}
