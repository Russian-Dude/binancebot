package com.rdude.binancebot.repository;

import com.rdude.binancebot.entity.SymbolSubscription;
import com.rdude.binancebot.entity.SymbolSubscriptionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SymbolSubscriptionRepository extends JpaRepository<SymbolSubscription, SymbolSubscriptionId> {
}
