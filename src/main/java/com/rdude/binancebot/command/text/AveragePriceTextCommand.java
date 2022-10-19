package com.rdude.binancebot.command.text;

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
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.math.BigDecimal;

@Component
@Slf4j
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
    protected BotApiMethod<?> execute(BotUser user, long chatId, String text) {
        if (text.equals(COMMAND_TEXT)) {
            BotUserState botUserState = user.getBotUserState();
            botUserState.setChatState(ChatState.ENTER_SYMBOL_PAIR_GET_AVG_PRICE);
            botUserStateService.save(botUserState);
            return messageSender.generate(user, ReplyMessage.ENTER_SYMBOL_PAIR);
        }
        else {
            String[] args = getArgs(COMMAND_TEXT, text);
            // in format "BTCUSDT"
            if (args.length == 1) {
                return binanceApiCaller.averagePrice(args[0])
                        .map(price -> {
                            BigDecimal p = price.getPrice();
                            if (p == null) return messageSender.generate(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                            else return messageSender.generateFormatted(
                                    user,
                                    ReplyMessage.AVERAGE_PRICE_X,
                                    args[0],
                                    price.getPrice());
                        })
                        .doOnError(e -> log.error("AveragePriceTextCommand error while calling BinanceApiCaller.price() with symbol " + args[0], e))
                        .onErrorReturn(messageSender.errorOccurred(user, chatId))
                        .block();
            }
            // in format "BTC, USDT"
            else if (args.length == 2) {
                String currency1 = args[0];
                String currency2 = args[1];
                return binanceApiCaller.averagePrice(currency1, currency2)
                        .map(price -> {
                            BigDecimal p = price.getPrice();
                            if (p == null) return messageSender.generate(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
                            else return messageSender.generateFormatted(
                                    user,
                                    ReplyMessage.AVERAGE_PRICE_X_TO_Y,
                                    currency1,
                                    currency2,
                                    price.getPrice()
                            );
                        })
                        .doOnError(e -> log.error("AveragePriceTextCommand error while calling BinanceApiCaller.price() with currencies " + currency1 + ", " + currency2, e))
                        .onErrorReturn(messageSender.generate(user, ReplyMessage.WRONG_SYMBOLS_PAIR))
                        .block();
            } else {
                return messageSender.generate(user, ReplyMessage.WRONG_SYMBOLS_PAIR);
            }
        }
    }
}
