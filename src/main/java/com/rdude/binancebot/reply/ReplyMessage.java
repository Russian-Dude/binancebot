package com.rdude.binancebot.reply;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Locale;
import java.util.ResourceBundle;

@AllArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public enum ReplyMessage {

    UNKNOWN_COMMAND("unknown_command"),
    ERROR_OCCURRED("error_occurred"),
    NOT_REGISTERED_USER("not_registered_user"),
    MAIN_MENU("main_menu"),
    ENTER_SYMBOL_PAIR("enter_symbol_pair"),
    WRONG_SYMBOLS_PAIR("wrong_symbols_pair"),
    AVERAGE_PRICE_X_TO_Y("average_price_x_to_y"),
    AVERAGE_PRICE_X("average_price_x"),
    PRICE_X("price_x"),
    PRICE_X_TO_Y("price_x_to_y"),
    PRICE_CHANGED("price_changed"),
    ENTER_PERCENT("enter_percent"),
    WRONG_PERCENT("wrong_percent"),
    SUBSCRIBED("subscribed"),
    SUBSCRIPTION("subscription")

    ;

    String messageKey;

    public String getMessage(Locale locale) {
        locale = locale == null ? Locale.ENGLISH : locale;
        return ResourceBundle.getBundle("messages", locale).getString(messageKey);
    }

}
