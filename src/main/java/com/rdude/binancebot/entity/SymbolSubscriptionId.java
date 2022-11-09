package com.rdude.binancebot.entity;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class SymbolSubscriptionId implements Serializable {

    Long botUser;

    String currency1;

    String currency2;

}
