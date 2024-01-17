package com.tradingsystem.application.services;

import com.tradingsystem.application.core.Order;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<UUID, Order> orderBooks;
    private final Map<UUID, Order> completedOrderBooks;
    private final ExchangeService exchangeService;
    private final InstrumentService instrumentService;

    @Autowired
    public OrderService(ExchangeService exchangeService, InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
        this.orderBooks = new ConcurrentHashMap<>();
        this.completedOrderBooks = new ConcurrentHashMap<>();
        this.exchangeService = exchangeService;
    }

    public void placeOrder(Order order) {
        orderBooks.put(order.getOrderId(), order);
    }

    public Order getOrderById(UUID orderId) {
        if(orderBooks.containsKey(orderId)) return orderBooks.get(orderId);
        return completedOrderBooks.get(orderId);
    }

    public List<Order> getOrders() {
        return orderBooks.values().stream().toList();
    }

    public List<Order> getCompletedOrders() {
        return completedOrderBooks.values().stream().toList();
    }

    // Should match orders every 1 second
    @Scheduled(fixedDelay = 1000)
    private void matchOrders() throws Exception {
        exchangeService.execute(orderBooks, completedOrderBooks, instrumentService.getInstruments());
    }
}
