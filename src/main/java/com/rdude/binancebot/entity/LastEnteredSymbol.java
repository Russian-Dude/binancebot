package com.rdude.binancebot.entity;

import com.rdude.binancebot.dto.Symbol;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "last_entered_symbol")
public class LastEnteredSymbol {

    @Id
    @Column(name = "bot_user_chat_id")
    private Long botUserChatId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "bot_user_chat_id", referencedColumnName = "chat_id", foreignKey = @ForeignKey(name = "bot_user_key"))
    private BotUser botUser;

    @Column(name = "currency1")
    private String currency1;

    @Column(name = "currency2")
    private String currency2;

    public LastEnteredSymbol(BotUser botUser) {
        this.botUser = botUser;
    }

    public Symbol getSymbol() {
        return new Symbol(currency1, currency2);
    }
}
