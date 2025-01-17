package com.nnk.springboot.repositories;

import com.nnk.springboot.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Trade repository
 */
public interface TradeRepository extends JpaRepository<Trade, Integer> {
}
