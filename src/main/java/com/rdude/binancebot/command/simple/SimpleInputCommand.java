package com.rdude.binancebot.command.simple;

import com.rdude.binancebot.command.Command;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public abstract class SimpleInputCommand {

    protected final BotUserService botUserService;

    protected final MessageSender messageSender;

    public abstract ChatState requiredState();

    public final BotApiMethod<?> execute(long chatId, String text) {
        BotUser user = null;
        try {
            user = botUserService.findByChatID(chatId).orElse(null);
            if (user != null) return execute(user, chatId, text);
            else return messageSender.notRegisteredUser(chatId);
        }
        catch (Exception e) {
            return messageSender.errorOccurred(user, chatId);
        }
    }

    public abstract BotApiMethod<?> execute(BotUser user, long chatId, String text);

    public boolean isUserInRequiredState(BotUser user) {
        return user.getBotUserState() != null && Objects.equals(user.getBotUserState().getChatState(), requiredState());
    }

    protected String[] getCurrenciesFromString(String text) {
        return text
                .strip()
                .toUpperCase()
                .split("[^A-Z]+");
    }

}
