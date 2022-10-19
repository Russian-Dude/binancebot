package com.rdude.binancebot.service;

import com.rdude.binancebot.entity.BotUserState;
import com.rdude.binancebot.repository.BotUserStateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BotUserStateService {

    private final BotUserStateRepository botUserStateRepository;

    public BotUserState save(BotUserState botUserState) {
        return botUserStateRepository.saveAndFlush(botUserState);
        //return botUserStateRepository.save(botUserState);
    }

}
