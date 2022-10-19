package com.rdude.binancebot.entity;

import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.state.ChatState;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "bot_user_state")
public class BotUserState {

    @Id
    @Column(name = "bot_user_chat_id")
    private Long botUserChatId;

    @MapsId
    @OneToOne
    @JoinColumn(name = "bot_user_chat_id", referencedColumnName = "chat_id", foreignKey = @ForeignKey(name = "bot_user_key"))
    private BotUser botUser;

    @Enumerated
    @Column(name = "chat_state")
    private ChatState chatState = ChatState.MAIN_MENU;

    @Enumerated
    @Column(name = "last_reply")
    private ReplyMessage lastReply;

    public BotUserState(BotUser botUser) {
        this.botUser = botUser;
    }
}
