package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.reply.menu.MainInlineMenu;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Locale;

@Component
public class ChangeLanguageCallbackCommand extends CallbackCommand {

    private final static Locale LOCALE_RU = new Locale("ru", "RU");

    private final MainInlineMenu mainInlineMenu;


    public ChangeLanguageCallbackCommand(BotUserService botUserService,
                                         MessageSender messageSender,
                                         MainInlineMenu mainInlineMenu) {
        super(botUserService, messageSender);
        this.mainInlineMenu = mainInlineMenu;
    }

    @Override
    protected BotApiMethod<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery) {
        boolean isSameMessage = user.getBotUserState()
                .getLastReply()
                .getMessage(user.getLocale())
                .equals(callbackQuery.getMessage().getText());

        Locale locale = user.getLocale();
        locale = locale.equals(Locale.ENGLISH) ? LOCALE_RU : Locale.ENGLISH;
        user.setLocale(locale);
        botUserService.save(user);

        EditMessageText editMessageText = new EditMessageText();
        ReplyMessage lastReply = user.getBotUserState().getLastReply();
        if (isSameMessage) {
            editMessageText.setText(lastReply.getMessage(user.getLocale()));
        }
        editMessageText.setMessageId(callbackQuery.getMessage().getMessageId());
        editMessageText.setChatId(callbackQuery.getMessage().getChatId());
        editMessageText.setReplyMarkup((InlineKeyboardMarkup) mainInlineMenu.getMarkup(user));

        return editMessageText;
    }

    @Override
    public String getCallbackData() {
        return "change_language";
    }
}
