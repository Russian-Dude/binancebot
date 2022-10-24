package com.rdude.binancebot.handlers;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandler {

    private final MessageSender messageSender;

    private final BotUserService botUserService;

    public void handle(Throwable throwable, long chatId) {
        BotUser user = botUserService.findByChatID(chatId).orElse(null);
        log.error(throwable.getMessage(), throwable);
        messageSender.sendErrorOccurred(user, chatId)
                .exceptionally(__ -> {
                    log.error("Can not send error message to user {}", user);
                    return null;
                })
                .join();
    }

}
