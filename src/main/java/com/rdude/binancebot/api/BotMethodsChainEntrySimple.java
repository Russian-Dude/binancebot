package com.rdude.binancebot.api;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.function.Function;

@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public class BotMethodsChainEntrySimple<T extends Serializable> extends BotMethodsChainEntryBase<T> {

    private final BotApiMethod<T> method;

    @Override
    public BotApiMethod<T> getMethod(Serializable previousMethodResult) {
        return method;
    }
}
