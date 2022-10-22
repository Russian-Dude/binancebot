package com.rdude.binancebot.handlers;

import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.command.simple.SimpleInputCommand;
import com.rdude.binancebot.command.text.TextCommand;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MessageHandler {

    BotUserService botUserService;

    MessageSender messageSender;

    List<TextCommand> textCommands;

    List<SimpleInputCommand> simpleInputCommands;

    public BotMethodsChainEntry<?> processMessage(Message message) {
        String text = message.getText();
        long chatId = message.getChatId();

        if (message.isCommand()) return processCommand(chatId, text);
        else return processSimpleText(chatId, text);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private BotMethodsChainEntry<?> processCommand(long chatId, String text) {
        return textCommands.stream()
                .filter(command -> command.checkString(text))
                .findFirst()
                .map(command -> command.execute(chatId, text))
                .orElseGet(() -> (BotMethodsChainEntry) messageSender.send(chatId, ReplyMessage.UNKNOWN_COMMAND));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private BotMethodsChainEntry<?> processSimpleText(long chatId, String text) {
        Optional<BotUser> userByChatId = botUserService.findByChatID(chatId);
        if (userByChatId.isEmpty()) return messageSender.sendNotRegisteredUser(chatId);

        var user = userByChatId.get();
        return simpleInputCommands.stream()
                .filter(command -> command.isUserInRequiredState(user))
                .findFirst()
                .map(command -> command.execute(user, chatId, text))
                .orElseGet(() -> (BotMethodsChainEntry) messageSender.send(user, ReplyMessage.UNKNOWN_COMMAND));
    }
}
