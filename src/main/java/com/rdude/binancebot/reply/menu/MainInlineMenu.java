package com.rdude.binancebot.reply.menu;

import com.rdude.binancebot.entity.BotUser;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

@Component
public class MainInlineMenu extends InlineMenu {

    @Override
    public ReplyKeyboard getMarkup(BotUser user) {

        var subscriptionsButton = new InlineKeyboardButton();
        subscriptionsButton.setText(InlineButton.MY_SUBSCRIPTIONS.getText(user.getLocale()));
        subscriptionsButton.setCallbackData(InlineButton.MY_SUBSCRIPTIONS.getCallback());

        var changeLanguageButton = new InlineKeyboardButton();
        changeLanguageButton.setText(InlineButton.CHANGE_LANGUAGE.getText(user.getLocale()));
        changeLanguageButton.setCallbackData(InlineButton.CHANGE_LANGUAGE.getCallback());

        return new InlineKeyboardMarkup(List.of(List.of(subscriptionsButton, changeLanguageButton)));
    }
}
