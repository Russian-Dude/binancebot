package com.rdude.binancebot.service;

import com.rdude.binancebot.api.BotMethodsChain;
import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
@RequiredArgsConstructor
public class MessageEditor {

    private final BotUserStateService botUserStateService;


    public BotMethodsChainEntry<?> edit(Integer messageId, BotUser user, ReplyMessage newText) {
        return edit(messageId, user, newText, null);
    }

    public BotMethodsChainEntry<?> edit(Integer messageId, BotUser user, ReplyMessage newText, InlineKeyboardMarkup keyboard) {
        return edit(messageId, user, newText, newText.getMessage(user.getLocale()), keyboard);
    }

    public BotMethodsChainEntry<?> editFormatted(Integer messageId, BotUser user, ReplyMessage newText, Object... args) {
        return editFormatted(messageId, user, newText, null, args);
    }

    public BotMethodsChainEntry<?> editFormatted(Integer messageId, BotUser user, ReplyMessage newText, InlineKeyboardMarkup keyboard, Object... args) {
        return edit(messageId, user, newText, String.format(newText.getMessage(user.getLocale()), args), keyboard);
    }

    public BotMethodsChainEntry<?> removeKeyboard(Integer messageId, BotUser user) {
        BotUserState botUserState = user.getBotUserState();
        Integer lastMessageId = botUserState.getLastMessageId();
        boolean lastMessageHasMarkup = botUserState.isLastMessageHasMarkup();
        if (messageId.equals(lastMessageId) && lastMessageHasMarkup) {
            botUserState.setLastMessageHasMarkup(false);
            botUserStateService.save(botUserState);
        }

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(user.getChatId());
        editMessageReplyMarkup.setMessageId(messageId);
        return BotMethodsChain.create(editMessageReplyMarkup);
    }

    private BotMethodsChainEntry<?> edit(Integer messageId, BotUser user, ReplyMessage replyMessage, String newText, InlineKeyboardMarkup keyboard) {
        BotUserState botUserState = user.getBotUserState();
        if (botUserState.getLastMessageId().equals(messageId) && !botUserState.getLastReply().equals(replyMessage)) {
            botUserState.setLastReply(replyMessage);
            botUserState.setLastMessageHasMarkup(keyboard != null);
            botUserStateService.save(botUserState);
        }

        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(messageId);
        editMessageText.setChatId(user.getChatId());
        editMessageText.setText(newText);
        editMessageText.setReplyMarkup(keyboard);
        return BotMethodsChain.create(editMessageText);
    }

}
