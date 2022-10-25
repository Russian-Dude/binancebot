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
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@EqualsAndHashCode
public abstract class CallbackCommand implements Command {

    BotUserService botUserService;

    MessageSender messageSender;


    @SuppressWarnings({"rawtypes", "unchecked"})
    public Mono<?> execute(@NotNull CallbackQuery callbackQuery) {
        try {
            Long chatId = callbackQuery.getMessage().getChatId();
            return botUserService.findByChatID(chatId)
                    .map(user -> execute(user, callbackQuery))
                    .orElseGet(() -> (Mono) messageSender.sendNotRegisteredUser(chatId));
        }
        catch (Exception e) {
            return Mono.error(new RuntimeException("Error while executing callback command " + this.getClass().getSimpleName() + ". " + e.getMessage()));
        }
    }

    protected abstract Mono<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery);

    public abstract String getCallbackData();


}
