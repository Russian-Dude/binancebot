package com.rdude.binancebot.service;

import com.rdude.binancebot.entity.BotUser;
import com.rdude.binancebot.repository.BotUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class BotUserService {

    BotUserRepository botUserRepository;

    public BotUser getOrRegisterAndGet(long chatId) {
        return botUserRepository.findById(chatId)
                .orElseGet(() -> botUserRepository.save(new BotUser(chatId)));
    }

    public Optional<BotUser> findByChatID(long chatId) {
        return botUserRepository.findById(chatId);
    }

    public BotUser save(BotUser user) {
        return botUserRepository.save(user);
    }

}
