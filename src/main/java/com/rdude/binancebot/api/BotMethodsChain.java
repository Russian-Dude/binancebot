package com.rdude.binancebot.api;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.io.Serializable;

@UtilityClass
public class BotMethodsChain {

    public <T extends Serializable> BotMethodsChainEntry<T> create(BotApiMethod<T> method) {
        BotMethodsChainEntrySimple<T> entry = new BotMethodsChainEntrySimple<>(method);
        entry.setRootEntry(entry);
        return entry;
    }

}
