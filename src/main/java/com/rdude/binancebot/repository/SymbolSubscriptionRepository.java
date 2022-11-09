package com.rdude.binancebot.repository;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.entity.SymbolSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SymbolSubscriptionRepository extends JpaRepository<SymbolSubscription, SymbolSubscriptionId> {
    void deleteByBotUserAndCurrency1AndCurrency2(BotUser botUser, String currency1, String currency2);
    List<SymbolSubscription> findByBotUser(BotUser botUser);
}
