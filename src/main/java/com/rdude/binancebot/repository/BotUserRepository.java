package com.rdude.binancebot.repository;

import com.rdude.binancebot.entity.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotUserRepository extends JpaRepository<BotUser, Long> {
}
