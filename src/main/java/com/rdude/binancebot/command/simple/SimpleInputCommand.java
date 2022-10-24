package com.rdude.binancebot.command.simple;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
public abstract class SimpleInputCommand {

    protected final BotUserService botUserService;

    protected final MessageSender messageSender;

    public abstract ChatState requiredState();

    @SuppressWarnings({"rawtypes", "unchecked"})
    public final CompletableFuture<?> execute(long chatId, String text) {
        try {
            return botUserService.findByChatID(chatId)
                    .map(user -> execute(user, chatId, text))
                    .orElseGet(() -> (CompletableFuture) messageSender.sendNotRegisteredUser(chatId));
        }
        catch (Exception e) {
            return CompletableFuture.failedFuture(new RuntimeException("Error while executing simple input command \"" + text + "\" in SimpleInputCommand class: " + this.getClass().getSimpleName() + ". Exception message: " + e.getMessage()));
        }
    }

    public abstract CompletableFuture<?> execute(BotUser user, long chatId, String text);

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
