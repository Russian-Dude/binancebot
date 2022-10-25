package com.rdude.binancebot.handlers;

import com.rdude.binancebot.command.callback.CallbackCommand;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class CallbackQueryHandler {

    List<CallbackCommand> callbackCommands;


    public Mono<?> processCallbackQuery(@NotNull CallbackQuery callbackQuery) {
        return callbackCommands.stream()
                .filter(command -> command.getCallbackData().equals(callbackQuery.getData()))
                .findFirst()
                .map(command -> command.execute(callbackQuery))
                .orElseGet(() -> Mono.error(new RuntimeException("Failed to process callback query " + callbackQuery)));
    }
}
