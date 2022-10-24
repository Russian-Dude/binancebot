package com.rdude.binancebot.command.callback;

import com.rdude.binancebot.api.BotMethodsExecutor;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.reply.menu.MainInlineMenu;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.MessageEditor;
import com.rdude.binancebot.service.MessageSender;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Locale;
import java.util.concurrent.CompletableFuture;

@Component
public class ChangeLanguageCallbackCommand extends CallbackCommand {

    private final static Locale LOCALE_RU = new Locale("ru", "RU");

    private final MainInlineMenu mainInlineMenu;

    private final MessageEditor messageEditor;

    private final BotMethodsExecutor executor;


    public ChangeLanguageCallbackCommand(BotUserService botUserService,
                                         MessageSender messageSender,
                                         MainInlineMenu mainInlineMenu,
                                         MessageEditor messageEditor,
                                         BotMethodsExecutor executor) {
        super(botUserService, messageSender);
        this.mainInlineMenu = mainInlineMenu;
        this.messageEditor = messageEditor;
        this.executor = executor;
    }

    @Override
    protected CompletableFuture<?> execute(BotUser user, @NotNull CallbackQuery callbackQuery) {
        return executor.execute(() -> {
                    BotUserState botUserState = user.getBotUserState();
                    boolean sameMessage = botUserState
                            .getLastReply()
                            .getMessage(user.getLocale())
                            .equals(callbackQuery.getMessage().getText());
                    Locale locale = user.getLocale();
                    locale = locale.equals(Locale.ENGLISH) ? LOCALE_RU : Locale.ENGLISH;
                    user.setLocale(locale);
                    botUserService.save(user);
                    return sameMessage;
                })
                .thenCompose(sameMessage -> {
                    BotUserState botUserState = user.getBotUserState();
                    if (sameMessage) {
                        return messageEditor.edit(botUserState.getLastMessageId(), user, ReplyMessage.MAIN_MENU, mainInlineMenu.getMarkup(user));
                    }
                    else return CompletableFuture.completedFuture(null);
                });
    }

    @Override
    public String getCallbackData() {
        return "change_language";
    }
}
