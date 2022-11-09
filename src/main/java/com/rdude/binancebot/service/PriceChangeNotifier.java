package com.rdude.binancebot.service;

import com.rdude.binancebot.api.BinanceBot;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import reactor.core.publisher.Flux;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

@Component
@RequiredArgsConstructor
@EnableScheduling
public class PriceChangeNotifier {

    private final SymbolSubscriptionService symbolSubscriptionService;

    private final BinanceApiCaller binanceApiCaller;

    private final MessageSender messageSender;

    private final BinanceBot binanceBot;


    @Scheduled(fixedRateString = "${bot.price-check-period}")
    private void checkAndNotify() {
        symbolSubscriptionService.findAllGroupedBySymbol().forEach((symbol, subscriptions) -> {

            Flux<Message> messageFlux = binanceApiCaller.price(symbol)
                    .flatMapMany(symbolPrice -> Flux.fromIterable(subscriptions)
                            .mapNotNull(subscription -> changeNotificationOrNull(symbolPrice.getPrice(), subscription))
                            .filter(Objects::nonNull)
                            .flatMap(change -> messageSender.sendFormatted(
                                    change.getUser(),
                                    ReplyMessage.PRICE_CHANGED,
                                    symbol.getCurrency1(),
                                    symbol.getCurrency2(),
                                    change.getFrom(),
                                    change.getTo(),
                                    change.getPercent()
                            )));

            binanceBot.execute(messageFlux);
        });
    }

    private ChangeNotification changeNotificationOrNull(BigDecimal currentPrice, SymbolSubscription subscription) {
        BigDecimal lastNotifiedPrice = subscription.getLastNotifiedPrice();
        BigDecimal requiredPercent = subscription.getRequiredPercentForNotification();
        BigDecimal priceChange = currentPrice.subtract(lastNotifiedPrice).abs();
        BigDecimal priceChangePercent = priceChange.divide(lastNotifiedPrice, RoundingMode.DOWN).multiply(new BigDecimal(100));
        return priceChangePercent.compareTo(requiredPercent) >= 0
                ? new ChangeNotification(lastNotifiedPrice, currentPrice, priceChangePercent, subscription.getBotUser())
                : null;
    }


    @Data
    private static class ChangeNotification {
        private final BigDecimal from;
        private final BigDecimal to;
        private final BigDecimal percent;
        private final BotUser user;
    }

}
