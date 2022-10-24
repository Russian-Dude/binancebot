package com.rdude.binancebot.command.text;

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
public class AveragePriceTextCommand extends TextCommand {

    private static final String COMMAND_TEXT = "/average_price";

    private final BinanceApiCaller binanceApiCaller;


    public AveragePriceTextCommand(BotUserService botUserService, BotUserStateService botUserStateService, MessageSender messageSender, BinanceApiCaller binanceApiCaller) {
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
    protected CompletableFuture<?> execute(BotUser user, long chatId, String text) {
        if (text.equals(COMMAND_TEXT)) {
            BotUserState botUserState = user.getBotUserState();
            botUserState.setChatState(ChatState.ENTER_SYMBOL_PAIR_GET_AVG_PRICE);
            botUserStateService.save(botUserState);
            return messageSender.send(user, ReplyMessage.ENTER_SYMBOL_PAIR);
        }
        else {
            String[] args = getArgs(COMMAND_TEXT, text);
            // in format "BTCUSDT"
            if (args.length == 1) {
                return binanceApiCaller.averagePrice(args[0])
                        .map(price -> {
                            BigDecimal p = price.getPrice();
                            if (p == null) return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                            else return messageSender.sendFormatted(
                                    user,
                                    ReplyMessage.AVERAGE_PRICE_X,
                                    args[0],
                                    price.getPrice());
                        })
                        .onErrorResume(e -> Mono.just(CompletableFuture.failedFuture(new RuntimeException("AveragePriceTextCommand error while calling BinanceApiCaller.price() with symbol " + args[0] + ". Exception message: " + e.getMessage()))))
                        .block();
            }
            // in format "BTC, USDT"
            else if (args.length == 2) {
                String currency1 = args[0];
                String currency2 = args[1];
                return binanceApiCaller.averagePrice(currency1, currency2)
                        .map(price -> {
                            BigDecimal p = price.getPrice();
                            if (p == null) return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                            else return messageSender.sendFormatted(
                                    user,
                                    ReplyMessage.AVERAGE_PRICE_X_TO_Y,
                                    currency1,
                                    currency2,
                                    price.getPrice()
                            );
                        })
                        .onErrorResume(e -> Mono.just(CompletableFuture.failedFuture(new RuntimeException("AveragePriceTextCommand error while calling BinanceApiCaller.price() with currencies " + currency1 + ", " + currency2 + ". Exception message: " + e.getMessage()))))
                        .block();
            } else {
                return messageSender.send(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
            }
        }
    }
}
