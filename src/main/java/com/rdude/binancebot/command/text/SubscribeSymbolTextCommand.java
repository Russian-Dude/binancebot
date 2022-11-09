package com.rdude.binancebot.command.text;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class SubscribeSymbolTextCommand extends TextCommand {

    private static final String COMMAND_TEXT = "/subscribe";

    public SubscribeSymbolTextCommand(BotUserService botUserService, BotUserStateService botUserStateService, MessageSender messageSender) {
        super(botUserService, botUserStateService, messageSender);
    }

    @Override
    public boolean checkString(String text) {
        return COMMAND_TEXT.equals(text);
    }

    @Override
    public boolean isRequiresRegistration() {
        return true;
    }

    @Override
    protected Mono<?> execute(BotUser user, long chatId, String text) {
        BotUserState botUserState = user.getBotUserState();
        botUserState.setChatState(ChatState.ENTER_SYMBOL_PAIR_SUBSCRIBE);
        botUserStateService.save(botUserState);
        return messageSender.send(user, ReplyMessage.ENTER_SYMBOL_PAIR);
    }
}
