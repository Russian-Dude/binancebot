package com.rdude.binancebot.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BotMethodsChainEntryDependent<T extends Serializable> extends BotMethodsChainEntryBase<T> {

    private final Function<? extends Serializable, BotApiMethod<T>> method;

    @Override
    @SuppressWarnings("unchecked")
    public BotApiMethod<T> getMethod(Serializable previousMethodResult) {
        return ((Function<Serializable, BotApiMethod<T>>) method).apply(previousMethodResult);
    }
}
