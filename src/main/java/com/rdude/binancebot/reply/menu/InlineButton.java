package com.rdude.binancebot.reply.menu;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Locale;
import java.util.ResourceBundle;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@Getter
public enum InlineButton {

    CHANGE_LANGUAGE("change_language"),
    MY_SUBSCRIPTIONS("my_subscriptions"),
    UNSUBSCRIBE("unsubscribe")

    ;

    String callback;

    public String getText(Locale locale) {
        locale = locale == null ? Locale.ENGLISH : locale;
        return ResourceBundle.getBundle("buttons", locale).getString(callback);
    }
}
