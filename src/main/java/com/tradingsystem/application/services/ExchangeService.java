package com.tradingsystem.application.services;

import com.tradingsystem.application.algorithms.FIFOMatchingStrategy;
import com.tradingsystem.application.algorithms.MatchingStrategy;
import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class ExchangeService {
    private MatchingStrategy strategy = new FIFOMatchingStrategy(new InstrumentService());

    public MatchingStrategy getStrategy() {
        return strategy;
    }

    public void setStrategy(MatchingStrategy strategy){
        this.strategy = strategy;
    }

    public void execute(Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook, Map<String, Instrument> instruments) {
        strategy.matchOrders(orderBook, completedOrderBook);
    }
}

