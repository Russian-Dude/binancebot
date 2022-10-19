package com.rdude.binancebot.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString
@Table(name = "bot_user")
public class BotUser {

    @NotNull
    @Id
    @Column(name = "chat_id", nullable = false, updatable = false)
    private Long chatId;

    @Column(name = "locale")
    private Locale locale = Locale.ENGLISH;

    @OneToOne(mappedBy = "botUser", cascade = CascadeType.ALL)
    @ToString.Exclude
    private BotUserState botUserState;

    @OneToMany(mappedBy = "botUser", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<SymbolSubscription> symbolSubscriptions;

    public BotUser(Long chatId) {
        this.chatId = chatId;
        botUserState = new BotUserState(this);
    }
}
