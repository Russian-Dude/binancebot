package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.command.Command;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class CallbackCommand implements Command {

    BotUserService botUserService;

    MessageSender messageSender;


    public BotApiMethod<?> execute(@NotNull CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();
        BotUser user = null;
        try {
            user = botUserService.findByChatID(chatId).orElse(null);
            if (user != null) return execute(user, callbackQuery);
            else return messageSender.notRegisteredUser(chatId);
        }
        catch (Exception e) {
            return messageSender.errorOccurred(user, chatId);
        }
    }

    protected abstract BotApiMethod<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery);

    public abstract String getCallbackData();


}
