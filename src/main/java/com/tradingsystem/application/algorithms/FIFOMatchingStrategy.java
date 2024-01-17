package com.tradingsystem.application.algorithms;

import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.services.InstrumentService;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * first in should be matched first once match has been found
 */
@Service
public class FIFOMatchingStrategy extends MatchingStrategy {
    public FIFOMatchingStrategy(InstrumentService instrumentService) {
        super(instrumentService);

    }

    @Override
    public void matchOrders(Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook) {
        List<Order> sortedOrders = orderBook.values()
                .stream()
                .sorted(Comparator.comparing(Order::getCreatedTimestamp))
                .collect(Collectors.toList());

        execute(sortedOrders, orderBook, completedOrderBook);
    }
}
