package com.rdude.binancebot.api;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

public interface BotMethodsChainEntry<T extends Serializable> {

    BotApiMethod<T> getMethod(Serializable previousMethodResult);

    T execute(Serializable previousMethodResult, Function<BotApiMethod<T>, T> action);

    BotMethodsChainEntry<?> getNextEntry();

    void setNextEntry(BotMethodsChainEntry<?> newNextEntry);

    BotMethodsChainEntry<?> getRootEntry();

    void setRootEntry(BotMethodsChainEntry<?> newRootEntry);

    <S extends Serializable> BotMethodsChainEntry<S> then(Function<T, BotApiMethod<S>> nextMethod);

    <S extends Serializable> BotMethodsChainEntry<S> then(BotApiMethod<S> nextMethod);

    <S extends Serializable> BotMethodsChainEntry<S> then(BotMethodsChainEntry<S> nextEntry);

    BotMethodsChainEntry<T> afterExecution(Consumer<T> action);

    // TODO: 21.10.2022 keep or remove
    //<S extends Serializable> BotMethodsChainEntry<S> thenFromRootOf(Function<T, BotMethodsChainEntry<S>> nextEntry);

}
