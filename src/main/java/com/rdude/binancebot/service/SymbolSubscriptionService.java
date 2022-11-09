package com.rdude.binancebot.service;

import com.rdude.binancebot.dto.Symbol;
import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.repository.SymbolSubscriptionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

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



}
