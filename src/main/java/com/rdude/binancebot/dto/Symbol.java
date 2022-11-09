package com.rdude.binancebot.dto;

import lombok.Data;

@Data
public class Symbol {
    private final String currency1;
    private final String currency2;

    public String getSymbolString() {
        return currency1 + currency2;
    }
}