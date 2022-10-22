package com.rdude.binancebot.command.text;

import com.rdude.binancebot.api.BotMethodsChainEntry;
import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.service.BotUserService;
import com.rdude.binancebot.service.BotUserStateService;
import com.rdude.binancebot.service.MessageSender;
import org.springframework.stereotype.Component;

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
    protected BotMethodsChainEntry<?> execute(BotUser user, long chatId, String text) {
        if (user == null) user = botUserService.save(new BotUser(chatId));
        return mainMenuCommand.execute(user, chatId, text);
    }
}
