package com.rdude.binancebot.service;

import com.rdude.binancebot.dto.Symbol;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.repository.SymbolSubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SymbolSubscriptionService {

    private final SymbolSubscriptionRepository repository;

    public SymbolSubscription save(SymbolSubscription symbolSubscription) {
        return repository.save(symbolSubscription);
    }

    public Map<Symbol, List<SymbolSubscription>> findAllGroupedBySymbol() {
        return repository.findAll().stream()
                .collect(Collectors.groupingBy(SymbolSubscription::getSymbol));
    }

    public List<SymbolSubscription> findByUser(BotUser user) {
        return repository.findByBotUser(user);
    }

    public void delete(SymbolSubscription subscription) {
        repository.delete(subscription);
    }

    public void delete(BotUser user, String currency1, String currency2) {
        repository.deleteByBotUserAndCurrency1AndCurrency2(user, currency1, currency2);
    }


}
