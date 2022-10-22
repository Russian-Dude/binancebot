package com.rdude.binancebot.api;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Function;

@Getter
@Setter
public abstract class BotMethodsChainEntryBase<T extends Serializable> implements BotMethodsChainEntry<T> {

    private BotMethodsChainEntry<?> rootEntry = null;

    private BotMethodsChainEntry<?> nextEntry = null;

    protected Consumer<T> afterExecutionFun = null;


    @Override
    public final T execute(Serializable previousMethodResult, Function<BotApiMethod<T>, T> action) {
        BotApiMethod<T> method = getMethod(previousMethodResult);
        T result = action.apply(method);
        if (afterExecutionFun != null) afterExecutionFun.accept(result);
        return result;
    }

    @Override
    public <S extends Serializable> BotMethodsChainEntry<S> then(Function<T, BotApiMethod<S>> nextMethod) {
        checkNextEntryAlreadyDefined();
        var newEntry = new BotMethodsChainEntryDependent<>(nextMethod);
        newEntry.setRootEntry(getRootEntry());
        this.nextEntry = newEntry;
        return newEntry;
    }

    @Override
    public <S extends Serializable> BotMethodsChainEntry<S> then(BotApiMethod<S> nextMethod) {
        checkNextEntryAlreadyDefined();
        var newEntry = new BotMethodsChainEntrySimple<>(nextMethod);
        newEntry.setRootEntry(getRootEntry());
        this.nextEntry = newEntry;
        return newEntry;
    }

    @Override
    public <S extends Serializable> BotMethodsChainEntry<S> then(BotMethodsChainEntry<S> nextEntry) {
        checkNextEntryAlreadyDefined();
        setNextEntry(nextEntry.getRootEntry());
        setNewRootOfChain(nextEntry, getRootEntry());
        return nextEntry;
    }

    @Override
    public BotMethodsChainEntry<T> afterExecution(Consumer<T> action) {
        if (afterExecutionFun == null) {
            afterExecutionFun = action;
        }
        else {
            var prevFun = afterExecutionFun;
            afterExecutionFun = t -> {
                prevFun.accept(t);
                action.accept(t);
            };
        }
        return this;
    }

    // TODO: 21.10.2022 keep or remove
/*    @Override
    public <S extends Serializable> BotMethodsChainEntry<S> thenFromRootOf(Function<T, BotMethodsChainEntry<S>> nextEntry) {
        checkNextEntryAlreadyDefined();
        Function<T, BotMethodsChainEntry<S>> proxyFunction = t -> {
            BotMethodsChainEntry<S> oldEntry = nextEntry.apply(t);
            setNewRootOfChain(oldEntry, getRootEntry());

        };
        BotMethodsChainEntryDependent<S> proxyEntry = new BotMethodsChainEntryDependent<>()
    }*/

    private void checkNextEntryAlreadyDefined() {
        if (nextEntry != null) throw new IllegalStateException("Next method chain entry is already defined");
    }

    private void setNewRootOfChain(BotMethodsChainEntry<?> anyChainEntry, BotMethodsChainEntry<?> newRoot) {
        BotMethodsChainEntry<?> oldRoot = anyChainEntry.getRootEntry();
        if (oldRoot == null || newRoot == null) throw new NullPointerException("Root of bot methods chain entry not initialized");
        var currentEntry = oldRoot;
        while (currentEntry != null) {
            currentEntry.setRootEntry(newRoot);
            currentEntry = currentEntry.getNextEntry();
        }
    }
}
