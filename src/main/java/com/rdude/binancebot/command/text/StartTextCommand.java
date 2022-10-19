package com.rdude.binancebot.command.text;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

@Component
public class StartTextCommand extends TextCommand {

    private final MainMenuTextCommand mainMenuCommand;

    public StartTextCommand(BotUserService botUserService, BotUserStateService botUserStateService, MessageSender messageSender, MainMenuTextCommand mainMenuCommand) {
        super(botUserService, botUserStateService, messageSender);
        this.mainMenuCommand = mainMenuCommand;
    }

    @Override
    public boolean checkString(String text) {
        return text.equals("/start");
    }

    @Override
    public boolean isRequiresRegistration() {
        return false;
    }

    @Override
    protected BotApiMethod<?> execute(BotUser user, long chatId, String text) {
        if (user != null) return mainMenuCommand.execute(user, chatId, text);

        user = botUserService.save(new BotUser(chatId));
        return mainMenuCommand.execute(user, chatId, text);
    }
}
