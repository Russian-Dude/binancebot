package com.rdude.binancebot.repository;

import com.rdude.binancebot.entity.LastEnteredSymbol;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LastEnteredSymbolRepository extends JpaRepository<LastEnteredSymbol, Long> {
}