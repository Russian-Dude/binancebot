package com.rdude.binancebot.service;

import com.rdude.binancebot.api.BotMethodsChain;
import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class MessageSender {

    private final BotUserService botUserService;

    private final BotUserStateService botUserStateService;

    private final MessageFactory messageFactory;


    public BotMethodsChainEntry<Message> send(long chatId, ReplyMessage message) {
        return send(chatId, message, null);
    }

    public BotMethodsChainEntry<Message> send(long chatId, ReplyMessage message, ReplyKeyboard keyboard) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> send(user, message, keyboard))
                .orElseGet(() -> sendNotRegisteredUser(chatId));
    }

    public BotMethodsChainEntry<Message> send(BotUser user, ReplyMessage message) {
        return send(user, message, null);
    }

    public BotMethodsChainEntry<Message> send(BotUser user, ReplyMessage message, ReplyKeyboard keyboard) {
        String text = message.getMessage(user.getLocale());
        return send(user, message, text, keyboard);
    }

    public BotMethodsChainEntry<Message> sendNotRegisteredUser(long chatId) {
        SendMessage sendMessage = messageFactory.generate(chatId, ReplyMessage.NOT_REGISTERED_USER.getMessage(Locale.ENGLISH), null);
        return BotMethodsChain.create(sendMessage);
    }

    public BotMethodsChainEntry<Message> sendErrorOccurred(BotUser user, long chatId) {
        if (user != null) {
            return send(user, ReplyMessage.ERROR_OCCURRED);
        }
        else {
            SendMessage sendMessage = messageFactory.generate(chatId, ReplyMessage.ERROR_OCCURRED.getMessage(Locale.ENGLISH), null);
            return BotMethodsChain.create(sendMessage);
        }
    }

    public BotMethodsChainEntry<Message> sendFormatted(long chatId, ReplyMessage message, Object ... args) {
        return sendFormatted(chatId, message, null, args);
    }

    public BotMethodsChainEntry<Message> sendFormatted(long chatId, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        return botUserService.findByChatID(chatId).stream()
                .findFirst()
                .map(user -> sendFormatted(user, message, keyboard))
                .orElseGet(() -> sendNotRegisteredUser(chatId));
    }

    public BotMethodsChainEntry<Message> sendFormatted(BotUser user, ReplyMessage message, Object ... args) {
        return sendFormatted(user, message, null, args);
    }

    public BotMethodsChainEntry<Message> sendFormatted(BotUser user, ReplyMessage message, ReplyKeyboard keyboard, Object ... args) {
        String text = String.format(message.getMessage(user.getLocale()), args);
        return send(user, message, text, keyboard);
    }

    private BotMethodsChainEntry<Message> send(BotUser user, ReplyMessage message, String text, ReplyKeyboard keyboard) {
        BotUserState botUserState = user.getBotUserState();
        Integer lastMessageId = botUserState.getLastMessageId();
        Long chatId = user.getChatId();

        BotMethodsChainEntry<?> editMarkupResult = null;
        if (botUserState.isLastMessageHasMarkup() && lastMessageId != null) {
            var editMessageReplyMarkup = new EditMessageReplyMarkup();
            editMessageReplyMarkup.setMessageId(lastMessageId);
            editMessageReplyMarkup.setChatId(chatId);
            editMessageReplyMarkup.setReplyMarkup(null);
            editMarkupResult = BotMethodsChain.create(editMessageReplyMarkup);
        }

        SendMessage sendMessage = messageFactory.generate(chatId, text, keyboard);

        BotMethodsChainEntry<Message> result;

        if (editMarkupResult != null) result = editMarkupResult.then(sendMessage);
        else result = BotMethodsChain.create(sendMessage);

        return result.afterExecution(mes -> {
            botUserState.setLastMessageId(mes.getMessageId());
            botUserState.setLastReply(message);
            botUserState.setLastMessageHasMarkup(keyboard != null);
            botUserStateService.save(botUserState);
        });
    }
}
