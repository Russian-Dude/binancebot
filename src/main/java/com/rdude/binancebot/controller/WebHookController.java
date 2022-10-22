package com.rdude.binancebot.controller;

import com.rdude.binancebot.api.BinanceBot;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@AllArgsConstructor
public class WebHookController {

    @Autowired
    private final BinanceBot binanceBot;

    // TODO: 20.10.2022 remove return type
    @PostMapping(value = "/")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return binanceBot.onWebhookUpdateReceived(update);
    }

}
