package com.rdude.binancebot.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class SymbolPrice {

    private String symbol;

    private BigDecimal price;

}