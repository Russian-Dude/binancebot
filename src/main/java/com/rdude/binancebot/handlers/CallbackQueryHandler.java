package com.rdude.binancebot.handlers;

import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.command.callback.CallbackCommand;
import com.rdude.binancebot.service.MessageSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.Serializable;
import java.util.List;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {

    MessageSender messageSender;

    List<CallbackCommand> callbackCommands;


    @SuppressWarnings({"unchecked", "rawtypes"})
    public BotMethodsChainEntry<?> processCallbackQuery(@NotNull CallbackQuery callbackQuery) {
        return callbackCommands.stream()
                .filter(command -> command.getCallbackData().equals(callbackQuery.getData()))
                .findFirst()
                .map(command -> command.execute(callbackQuery))
                .orElseGet(() -> (BotMethodsChainEntry) messageSender.sendErrorOccurred(null, callbackQuery.getMessage().getChatId()));
    }
}
