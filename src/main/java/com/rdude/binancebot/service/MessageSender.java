package com.rdude.binancebot.service;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final BotUserService botUserService;

    private final BotUserStateService botUserStateService;

    private final MessageFactory messageFactory;


    public SendMessage generate(long chatId, ReplyMessage message) {
        return generate(chatId, message, null);
    }

    public SendMessage generate(long chatId, ReplyMessage message, ReplyKeyboard keyboard) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> generate(user, message, keyboard))
                .orElseGet(() -> notRegisteredUser(chatId));
    }

    public SendMessage generate(BotUser user, ReplyMessage message) {
        return generate(user, message, null);
    }

    public SendMessage generate(BotUser user, ReplyMessage message, ReplyKeyboard keyboard) {
        String text = message.getMessage(user.getLocale());
        BotUserState botUserState = user.getBotUserState();
        botUserState.setLastReply(message);
        botUserStateService.save(botUserState);
        return generate(user.getChatId(), text, keyboard);
    }

    public SendMessage notRegisteredUser(long chatId) {
        return generate(chatId, ReplyMessage.NOT_REGISTERED_USER.getMessage(Locale.ENGLISH), null);
    }

    public SendMessage errorOccurred(BotUser user, long chatId) {
        Locale locale = user == null ? Locale.ENGLISH : user.getLocale();
        return generate(chatId, ReplyMessage.ERROR_OCCURRED.getMessage(locale));
    }

    public SendMessage generateFormatted(long chatId, ReplyMessage message, Object ... args) {
        return generateFormatted(chatId, message, null, args);
    }

    public SendMessage generateFormatted(long chatId, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> generateFormatted(user, message, keyboard))
                .orElseGet(() -> notRegisteredUser(chatId));
    }

    public SendMessage generateFormatted(BotUser user, ReplyMessage message, Object ... args) {
        return generateFormatted(user, message, null, args);
    }

    public SendMessage generateFormatted(BotUser user, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        String text = String.format(message.getMessage(user.getLocale()), args);
        BotUserState botUserState = user.getBotUserState();
        botUserState.setLastReply(message);
        botUserStateService.save(botUserState);
        return generate(user.getChatId(), text, keyboard);
    }

    private SendMessage generate(long chatId, String text) {
        return generate(chatId, text, null);
    }

    private SendMessage generate(long chatId, String text, ReplyKeyboard keyboard) {
        return messageFactory.generate(chatId, text, keyboard);
    }

}
