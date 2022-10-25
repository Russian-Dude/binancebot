package com.rdude.binancebot.command.text;

import com.rdude.binancebot.command.Command;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

/**
 * Base class for all bot text commands. Extend it to add a new command.
 */
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class TextCommand implements Command {

    BotUserService botUserService;

    BotUserStateService botUserStateService;

    MessageSender messageSender;


    public Mono<?> execute(long chatId, String text) {
        try {
            BotUser user = botUserService.findByChatID(chatId).orElse(null);
            if (user == null && isRequiresRegistration()) return messageSender.sendNotRegisteredUser(chatId);
            else return execute(user, chatId, text);
        }
        catch (Exception e) {
            return Mono.error(new RuntimeException("Error while executing text command \"" + text + "\" in TextCommand class: " + this.getClass().getSimpleName() + ". Exception message: " + e.getMessage()));
        }
    }

    public abstract boolean checkString(String text);

    /**
     * If this method returns true and the user that send a command is not registered in the DB,
     * execute method will not be executed and "not_registered_user" message will be sent instead.
     */
    public abstract boolean isRequiresRegistration();

    protected abstract Mono<?> execute(BotUser user, long chatId, String text);

    protected String[] getArgs(String command, String text) {
        return text
                .replaceFirst(command, "")
                .strip()
                .toUpperCase()
                .split("[^A-Z]+");
    }
}
