package com.rdude.binancebot.reply.menu;

import com.rdude.binancebot.entity.BotUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

@Component
public abstract class InlineMenu {

    public abstract ReplyKeyboard getMarkup(BotUser user);

}
