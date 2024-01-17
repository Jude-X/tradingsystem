package com.tradingsystem.application.algorithms;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.core.OrderStatus;
import com.tradingsystem.application.core.OrderType;
import com.tradingsystem.application.services.InstrumentService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class MatchingStrategy {

    private final InstrumentService instrumentService;
    private static final Logger logger = Logger.getLogger(MatchingStrategy.class.getName());

    protected MatchingStrategy(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    public abstract void matchOrders(Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook);

    void execute(List<Order> sortedOrders, Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook) {
        for (Order order : sortedOrders) {
            if (order.getOrderStatus() == OrderStatus.CREATED) {
                if (order.getOrderType().equals(OrderType.BUY)) {
                    matchOrder(order, orderBook, completedOrderBook, OrderType.SELL);
                } else if (order.getOrderType().equals(OrderType.SELL)) {
                    matchOrder(order, orderBook, completedOrderBook, OrderType.BUY);
                }
            }
        }
    }

    void matchOrder(Order order, Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook, OrderType complementType) {
        boolean tradeMatched = false;

        for (Order complementOrder : orderBook.values()) {
            // Check if there is a matching sell order that has the same price and the same instrument
            if (complementOrder.getOrderType().equals(complementType) && complementOrder.getOrderStatus() == OrderStatus.CREATED
                    && order.getInstrument().getMarketPrice().compareTo(complementOrder.getInstrument().getMarketPrice()) == 0) {
                // For simplicity, I'm only matching the same instrument together (whether composite or not)
                if (complementOrder.getInstrument().equals(order.getInstrument())) {
                    executeTrade(order, complementOrder, orderBook, completedOrderBook);
                    tradeMatched = true;
                }
                if (order.getCurrentQuantity().compareTo(BigDecimal.ZERO) <= 0) {
                    break;  // No more quantity to match
                }
            }
        }

        if (!tradeMatched) {
            // Log when no matching trade is found
            logger.log(Level.INFO, "No matching trade found for order {0} of {1} shares at price {2}",
                    new Object[]{order.getOrderId(), order.getCurrentQuantity(), order.getInstrument().getSymbol(), order.getPrice()});
        }
    }

    private void executeTrade(Order buyOrder, Order sellOrder, Map<UUID, Order> orderBook, Map<UUID, Order> completedOrderBook) {
        // Determine the trade quantity based on the minimum of both orders
        BigDecimal tradeQuantity = buyOrder.getCurrentQuantity().min(sellOrder.getCurrentQuantity());
        Instant now = Instant.now();
        // Log the trade execution
        logger.log(Level.INFO, "Trade executed: {0} shares of {1} at price {2}",
                new Object[]{tradeQuantity, buyOrder.getInstrument().getSymbol(), buyOrder.getPrice()});

        // Update order quantities
        buyOrder.decreaseQuantity(tradeQuantity);
        sellOrder.decreaseQuantity(tradeQuantity);

        // Move completed orders to the completedOrderBook map
        if (buyOrder.getCurrentQuantity().compareTo(BigDecimal.ZERO) == 0) {
            buyOrder.setOrderStatus(OrderStatus.EXECUTED);
            buyOrder.setExecutedTimestamp(now);
            completedOrderBook.put(buyOrder.getOrderId(), buyOrder);
            orderBook.remove(buyOrder.getOrderId());
        }

        if (sellOrder.getCurrentQuantity().compareTo(BigDecimal.ZERO) == 0) {
            sellOrder.setOrderStatus(OrderStatus.EXECUTED);
            sellOrder.setExecutedTimestamp(now);
            completedOrderBook.put(sellOrder.getOrderId(), sellOrder);
            orderBook.remove(sellOrder.getOrderId());
        }
    }
}
