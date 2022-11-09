package com.rdude.binancebot.entity;

import com.rdude.binancebot.dto.Symbol;
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
    @Column(name = "currency_1")
    private String currency1;

    @Id
    @Column(name = "currency_2")
    private String currency2;

    @Id
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bot_user_chat_id", referencedColumnName = "chat_id", foreignKey = @ForeignKey(name = "bot_user_key"))
    private BotUser botUser;

    @Column(name = "required_percent_for_notification")
    private BigDecimal requiredPercentForNotification;

    @Column(name = "last_notified_price")
    private BigDecimal lastNotifiedPrice;


    public Symbol getSymbol() {
        return new Symbol(currency1, currency2);
    }

}
