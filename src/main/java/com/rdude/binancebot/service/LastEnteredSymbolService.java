package com.rdude.binancebot.service;

import com.rdude.binancebot.dto.Symbol;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.LastEnteredSymbol;
import com.rdude.binancebot.repository.LastEnteredSymbolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LastEnteredSymbolService {

    private final LastEnteredSymbolRepository repository;

    public Symbol findByUser(BotUser user) {
        return repository.findById(user.getChatId())
                .map(LastEnteredSymbol::getSymbol)
                .orElse(null);
    }

    public LastEnteredSymbol save(LastEnteredSymbol entity) {
        return repository.saveAndFlush(entity);
    }

    public LastEnteredSymbol save(BotUser user, Symbol symbol) {
        LastEnteredSymbol lastEnteredSymbol = repository
                .findById(user.getChatId())
                .orElse(new LastEnteredSymbol(user));
        lastEnteredSymbol.setCurrency1(symbol.getCurrency1());
        lastEnteredSymbol.setCurrency2(symbol.getCurrency2());
        return repository.saveAndFlush(lastEnteredSymbol);
    }
}
