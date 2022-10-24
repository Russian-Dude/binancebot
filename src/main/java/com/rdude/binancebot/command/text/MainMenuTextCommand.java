package com.rdude.binancebot.command.text;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.reply.ReplyMessage;
import com.rdude.binancebot.reply.menu.MainInlineMenu;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import com.rdude.binancebot.state.ChatState;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class MainMenuTextCommand extends TextCommand {

    MainInlineMenu mainInlineMenu;

    public MainMenuTextCommand(BotUserService botUserService, BotUserStateService botUserStateService, MessageSender messageSender, MainInlineMenu mainInlineMenu) {
        super(botUserService, botUserStateService, messageSender);
        this.mainInlineMenu = mainInlineMenu;
    }


    @Override
    public boolean isRequiresRegistration() {
        return true;
    }

    @Override
    public boolean checkString(String text) {
        return "/menu".equals(text);
    }

    @Override
    protected CompletableFuture<?> execute(@NotNull BotUser user, long chatId, String text) {
        BotUserState botUserState = user.getBotUserState();
        botUserState.setChatState(ChatState.MAIN_MENU);
        botUserStateService.save(botUserState);
        return messageSender.send(user, ReplyMessage.MAIN_MENU, mainInlineMenu.getMarkup(user));
    }

}
