package com.rdude.binancebot.service;

import com.rdude.binancebot.api.BotMethodsExecutor;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import reactor.core.publisher.Mono;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final BotMethodsExecutor executor;

    private final BotUserService botUserService;

    private final BotUserStateService botUserStateService;

    private final MessageFactory messageFactory;


    public Mono<Message> send(long chatId, ReplyMessage message) {
        return send(chatId, message, null);
    }

    public Mono<Message> send(long chatId, ReplyMessage message, ReplyKeyboard keyboard) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> send(user, message, keyboard))
                .orElseGet(() -> sendNotRegisteredUser(chatId));
    }

    public Mono<Message> send(BotUser user, ReplyMessage message) {
        return send(user, message, null);
    }

    public Mono<Message> send(BotUser user, ReplyMessage message, ReplyKeyboard keyboard) {
        String text = message.getMessage(user.getLocale());
        return send(user, message, text, keyboard);
    }

    public Mono<Message> sendNotRegisteredUser(long chatId) {
        SendMessage sendMessage = messageFactory.generate(chatId, ReplyMessage.NOT_REGISTERED_USER.getMessage(Locale.ENGLISH), null);
        return executor.execute(sendMessage);
    }

    public Mono<Message> sendErrorOccurred(BotUser user, long chatId) {
        if (user != null) {
            return send(user, ReplyMessage.ERROR_OCCURRED);
        }
        else {
            SendMessage sendMessage = messageFactory.generate(chatId, ReplyMessage.ERROR_OCCURRED.getMessage(Locale.ENGLISH), null);
            return executor.execute(sendMessage);
        }
    }

    public Mono<Message> sendFormatted(long chatId, ReplyMessage message, Object ... args) {
        return sendFormatted(chatId, message, null, args);
    }

    public Mono<Message> sendFormatted(long chatId, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> sendFormatted(user, message, keyboard))
                .orElseGet(() -> sendNotRegisteredUser(chatId));
    }

    public Mono<Message> sendFormatted(BotUser user, ReplyMessage message, Object ... args) {
        return sendFormatted(user, message, null, args);
    }

    public Mono<Message> sendFormatted(BotUser user, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        String text = String.format(message.getMessage(user.getLocale()), args);
        return send(user, message, text, keyboard);
    }

    private Mono<Message> send(BotUser user, ReplyMessage message, String text, ReplyKeyboard keyboard) {
        BotUserState botUserState = user.getBotUserState();
        Integer lastMessageId = botUserState.getLastMessageId();
        Long chatId = user.getChatId();

        Mono<?> editMarkupResult = null;
        if (botUserState.isLastMessageHasMarkup() && lastMessageId != null) {
            var editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setMessageId(lastMessageId);
            editMessageReplyMarkup.setChatId(chatId);
            editMessageReplyMarkup.setReplyMarkup(null);
            editMarkupResult = executor.execute(editMessageReplyMarkup);
        }

        SendMessage sendMessage = messageFactory.generate(chatId, text, keyboard);

        Mono<Message> result;

        if (editMarkupResult != null) result = editMarkupResult.then(executor.execute(sendMessage));
        else result = executor.execute(sendMessage);

        return result.doOnSuccess(msg -> {
            botUserState.setLastMessageId(msg.getMessageId());
            botUserState.setLastReply(message);
            botUserState.setLastMessageHasMarkup(keyboard != null);
            botUserStateService.save(botUserState);
        });
    }
}
