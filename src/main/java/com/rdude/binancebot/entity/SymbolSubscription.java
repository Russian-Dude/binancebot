package com.rdude.binancebot.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Getter
@Setter
@IdClass(SymbolSubscriptionId.class)
@AllArgsConstructor
@NoArgsConstructor
public class SymbolSubscription {

    @Id
    @Column(name = "symbol_1")
    private String symbol1;

    @Id
    @Column(name = "symbol_2")
    private String symbol2;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bot_user_chat_id", referencedColumnName = "chat_id", foreignKey = @ForeignKey(name = "bot_user_key"))
    private BotUser botUser;

    @Column(name = "required_percent_for_notification")
    private BigDecimal requiredPercentForNotification;

    @Column(name = "last_notified_price")
    private BigDecimal lastNotifiedPrice;


    public String getSymbolName() {
        return symbol1 + symbol2;
    }

}
