package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.api.BotMethodsChain;
import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.reply.menu.MainInlineMenu;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageEditor;
import com.rdude.binancebot.service.MessageSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Locale;

@Component
public class ChangeLanguageCallbackCommand extends CallbackCommand {

    private final static Locale LOCALE_RU = new Locale("ru", "RU");

    private final MainInlineMenu mainInlineMenu;

    private final MessageEditor messageEditor;


    public ChangeLanguageCallbackCommand(BotUserService botUserService,
                                         MessageSender messageSender,
                                         MainInlineMenu mainInlineMenu, MessageEditor messageEditor) {
        super(botUserService, messageSender);
        this.mainInlineMenu = mainInlineMenu;
        this.messageEditor = messageEditor;
    }

    @Override
    protected BotMethodsChainEntry<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery) {
        BotUserState botUserState = user.getBotUserState();
        boolean isSameMessage = botUserState
                .getLastReply()
                .getMessage(user.getLocale())
                .equals(callbackQuery.getMessage().getText());

        Locale locale = user.getLocale();
        locale = locale.equals(Locale.ENGLISH) ? LOCALE_RU : Locale.ENGLISH;
        user.setLocale(locale);
        botUserService.save(user);

        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callbackQuery.getId())
                .text(ReplyMessage.UNKNOWN_COMMAND.getMessage(locale))
                .showAlert(true)
                .build();

        BotMethodsChainEntry<?> result = BotMethodsChain.create(answerCallbackQuery);

        if (isSameMessage) {
            result = result.then(messageEditor.edit(botUserState.getLastMessageId(), user, ReplyMessage.MAIN_MENU, mainInlineMenu.getMarkup(user)));
        }

        return result;
    }

    @Override
    public String getCallbackData() {
        return "change_language";
    }
}
