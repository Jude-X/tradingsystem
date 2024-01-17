package com.tradingsystem.application.unit;

import com.tradingsystem.application.algorithms.ProRataMatchingStrategy;
import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.core.OrderStatus;
import com.tradingsystem.application.core.OrderType;
import com.tradingsystem.application.services.InstrumentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProRataMatchingStrategyTest extends BaseUnitTest {

    private ProRataMatchingStrategy proRataMatchingStrategy;

    @BeforeEach
    void setUp() {
        proRataMatchingStrategy = new ProRataMatchingStrategy(new InstrumentService());
    }

    @Test
    void testProRataMatchOrders() {
        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();

        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        proRataMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        assertEquals(OrderStatus.EXECUTED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());

        assertEquals(BigDecimal.ZERO, buyOrder.getCurrentQuantity());
        assertEquals(BigDecimal.ZERO, sellOrder.getCurrentQuantity());

        assertEquals(BigDecimal.TEN, buyOrder.getInitialQuantity());
        assertEquals(BigDecimal.TEN, sellOrder.getInitialQuantity());
    }

    @Test
    void testProRataMatchOrders_MatchingOrders() {
        // Test when buy and sell orders match

        // Create orders with matching prices
        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        proRataMatchingStrategy.matchOrders(orderBook, new HashMap<>());

        // Verify that both orders are executed
        assertEquals(OrderStatus.EXECUTED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());

        // Verify that both orders are removed from the order book
        orderBook.remove(buyOrder.getOrderId());
        orderBook.remove(sellOrder.getOrderId());
        assertEquals(0, orderBook.size());
    }

    @Test
    void testProRataMatchOrders_PricesDoNotMatch() {
        // Test when buy and sell orders have different prices

        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.ONE, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        proRataMatchingStrategy.matchOrders(orderBook, new HashMap<>());

        // Verify that orders are not executed
        assertEquals(OrderStatus.CREATED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.CREATED, sellOrder.getOrderStatus());

        // Verify that orders are not removed from the order book
        assertEquals(2, orderBook.size());
    }

    @Test
    void testProRataMatchOrders_MultiplePricesMatch_LargestQuantityPriority() {
        // Test when multiple buy and sell orders have different prices, but the largest quantity should match first

        // Although the first buyer and first seller are created last, they will be matched and the earlier buyer wont be
        Order buyOrder2 = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.ONE);
        Order sellOrder1 = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);
        Order buyOrder1 = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder1.getOrderId(), buyOrder1);
        orderBook.put(buyOrder2.getOrderId(), buyOrder2);
        orderBook.put(sellOrder1.getOrderId(), sellOrder1);

        proRataMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that the first buy and sell orders are executed
        assertEquals(OrderStatus.EXECUTED, buyOrder1.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder1.getOrderStatus());

        // Verify that the second buy order is not executed
        assertEquals(OrderStatus.CREATED, buyOrder2.getOrderStatus());

        // Verify that the first buy and sell orders are not removed from the order book
        assertEquals(1, orderBook.size());
        assertEquals(2, completedOrderBook.size());
    }

    @Test
    void testProRataMatchOrders_MultipleOrdersQuantityReduced() {
        // Test when multiple buy and sell orders have their quantity reduced before getting exhausted

        Order buyOrder1 = createOrder( OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order buyOrder2 = createOrder(  OrderType.BUY, BigDecimal.ONE, BigDecimal.TEN);
        Order sellOrder1 = createOrder( OrderType.SELL, BigDecimal.ONE, BigDecimal.TEN);
        Order sellOrder2 = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder1.getOrderId(), buyOrder1);
        orderBook.put(buyOrder2.getOrderId(), buyOrder2);
        orderBook.put(sellOrder1.getOrderId(), sellOrder1);
        orderBook.put(sellOrder2.getOrderId(), sellOrder2);

        // Match only one quantity, so both orders will have their quantity reduced
        buyOrder1.setCurrentQuantity(BigDecimal.ONE);
        sellOrder1.setCurrentQuantity(BigDecimal.ONE);

        proRataMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that the first buy and sell orders are executed and removed from the order book
        assertEquals(OrderStatus.EXECUTED, buyOrder1.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder1.getOrderStatus());
        assertEquals(2, orderBook.size()); // Only one order should remain (whichever was created first)

        // Verify that the second buy and sell orders are not executed
        assertEquals(OrderStatus.CREATED, buyOrder2.getOrderStatus());
        assertEquals(OrderStatus.CREATED, sellOrder2.getOrderStatus());

        // Verify that the second buy and sell orders are not removed from the order book
        assertEquals(2, orderBook.size());
    }
}

