package com.rdude.binancebot.service;

import com.rdude.binancebot.api.BotMethodsExecutor;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class MessageEditor {

    private final BotUserStateService botUserStateService;

    private final BotMethodsExecutor executor;


    public Mono<?> edit(Integer messageId, BotUser user, ReplyMessage newText) {
        return edit(messageId, user, newText, null);
    }

    public Mono<?> edit(Integer messageId, BotUser user, ReplyMessage newText, InlineKeyboardMarkup keyboard) {
        return edit(messageId, user, newText, newText.getMessage(user.getLocale()), keyboard);
    }

    public Mono<?> editFormatted(Integer messageId, BotUser user, ReplyMessage newText, Object... args) {
        return editFormatted(messageId, user, newText, null, args);
    }

    public Mono<?> editFormatted(Integer messageId, BotUser user, ReplyMessage newText, InlineKeyboardMarkup keyboard, Object... args) {
        return edit(messageId, user, newText, String.format(newText.getMessage(user.getLocale()), args), keyboard);
    }

    public Mono<?> delete(Integer messageId, BotUser user) {
        return Mono.fromSupplier(() -> {
                    var deleteMessage = new DeleteMessage();
                    deleteMessage.setMessageId(messageId);
                    deleteMessage.setChatId(user.getChatId());
                    return deleteMessage;
                })
                .flatMap(executor::execute);
    }

    public Mono<?> removeKeyboard(Integer messageId, BotUser user) {
        return Mono.fromSupplier(() -> {
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
                    return editMessageReplyMarkup;
                })
                .flatMap(executor::execute);
    }

    private Mono<?> edit(Integer messageId, BotUser user, ReplyMessage replyMessage, String newText, InlineKeyboardMarkup keyboard) {
        return Mono.fromSupplier(() -> {
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
                    return editMessageText;
                })
                .flatMap(executor::execute);
    }

}
