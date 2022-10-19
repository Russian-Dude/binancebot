package com.rdude.binancebot.repository;

import com.rdude.binancebot.entity.BotUserState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotUserStateRepository extends JpaRepository<BotUserState, Long> {
}
