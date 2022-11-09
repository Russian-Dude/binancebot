package com.rdude.binancebot.reply.menu;

import com.rdude.binancebot.entity.BotUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class UnsubscribeInlineMenu extends InlineMenu {

    @Override
    public ReplyKeyboard getMarkup(BotUser user) {
        var button = new InlineKeyboardButton();
        button.setText(InlineButton.UNSUBSCRIBE.getText(user.getLocale()));
        button.setCallbackData(InlineButton.UNSUBSCRIBE.getCallback());
        return new InlineKeyboardMarkup(List.of(List.of(button)));
    }
}
