package com.rdude.binancebot.handlers;

import com.rdude.binancebot.command.callback.CallbackCommand;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.MessageSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {

    MessageSender messageSender;

    List<CallbackCommand> callbackCommands;


    public BotApiMethod<?> processCallbackQuery(@NotNull CallbackQuery callbackQuery) {
        return callbackCommands.stream()
                .filter(command -> command.getCallbackData().equals(callbackQuery.getData()))
                .map(command -> command.execute(callbackQuery))
                .findFirst()
                .orElseGet(() -> (BotApiMethod) messageSender.errorOccurred(null, callbackQuery.getMessage().getChatId()));
    }
}
