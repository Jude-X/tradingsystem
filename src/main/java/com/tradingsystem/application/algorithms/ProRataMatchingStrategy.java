package com.tradingsystem.application.algorithms;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.services.InstrumentService;

import java.util.Map;

/**
 * If multiple matches are found, the one with higher quantity should win
 */
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProRataMatchingStrategy extends MatchingStrategy {

    public ProRataMatchingStrategy(InstrumentService instrumentService) {
        super(instrumentService);
    }

    @Override
    public void matchOrders(Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook) {
        List<Order> sortedOrders = orderBook.values()
                .stream()
                .sorted(Comparator.comparing(Order::getInitialQuantity).reversed())
                .collect(Collectors.toList());

        execute(sortedOrders, orderBook, completedOrderBook);
    }
}

