package com.tradingsystem.application.unit;

import com.tradingsystem.application.algorithms.FIFOMatchingStrategy;
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

class FIFOMatchingStrategyTest extends BaseUnitTest {

    private InstrumentService instrumentService;

    private FIFOMatchingStrategy fifoMatchingStrategy;

    @BeforeEach
    void setUp() {
        instrumentService = new InstrumentService();
        fifoMatchingStrategy = new FIFOMatchingStrategy(instrumentService);
    }

    @Test
    void testFIFOMatchOrders() {
        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();

        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        fifoMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        assertEquals(OrderStatus.EXECUTED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());

        assertEquals(BigDecimal.ZERO, buyOrder.getCurrentQuantity());
        assertEquals(BigDecimal.ZERO, sellOrder.getCurrentQuantity());

        assertEquals(BigDecimal.TEN, buyOrder.getInitialQuantity());
        assertEquals(BigDecimal.TEN, sellOrder.getInitialQuantity());

        assertEquals(0, orderBook.size());
        assertEquals(2, completedOrderBook.size());
    }

    @Test
    void testFIFOMatchOrders_MatchingOrders() {
        // Test when buy and sell orders match

        // Create orders with matching prices
        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        fifoMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that both orders are executed
        assertEquals(OrderStatus.EXECUTED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder.getOrderStatus());

        // Verify that both orders are removed from the order book
        orderBook.remove(buyOrder.getOrderId());
        orderBook.remove(sellOrder.getOrderId());
        assertEquals(0, orderBook.size());
        assertEquals(2, completedOrderBook.size());
    }

    @Test
    void testFIFOMatchOrders_PricesDoNotMatch() {
        // Test when buy and sell orders have different prices

        Order buyOrder = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order sellOrder = createOrder(OrderType.SELL, BigDecimal.ONE, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new HashMap<>();
        orderBook.put(buyOrder.getOrderId(), buyOrder);
        orderBook.put(sellOrder.getOrderId(), sellOrder);

        fifoMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that orders are not executed
        assertEquals(OrderStatus.CREATED, buyOrder.getOrderStatus());
        assertEquals(OrderStatus.CREATED, sellOrder.getOrderStatus());

        // Verify that orders are not removed from the order book
        assertEquals(2, orderBook.size());
        assertEquals(0, completedOrderBook.size());
    }

    @Test
    void testFIFOMatchOrders_MultiplePricesMatch_FirstPriority() {
        // Test when multiple buy and sell orders have different prices, but the first one should match first

        // Buyer1 is first in line, and wants to purchase 10 units
        Order buyOrder1 = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        // Seller1 wants is first in line, wants to sell just one
        Order sellOrder1 = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.ONE);
        // Buyer2 is second in line, wants just one unit
        Order buyOrder2 = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.ONE);
        // Seller2 is 2nd in line wants to sell 9
        Order sellOrder2 = createOrder(OrderType.SELL, BigDecimal.TEN, new BigDecimal(9));

        // Behaviour should be that buyer 1 order is filled with one of seller1 and 9 of seller2, leaving buyer2 for more matches

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new ConcurrentHashMap<>();
        orderBook.put(buyOrder1.getOrderId(), buyOrder1);
        orderBook.put(buyOrder2.getOrderId(), buyOrder2);
        orderBook.put(sellOrder1.getOrderId(), sellOrder1);
        orderBook.put(sellOrder2.getOrderId(), sellOrder2);

        fifoMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that the first buy and 2 sell orders are executed
        assertEquals(OrderStatus.EXECUTED, buyOrder1.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder1.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder2.getOrderStatus());

        // Verify that the second buy orders are not executed
        assertEquals(OrderStatus.CREATED, buyOrder2.getOrderStatus());
        assertEquals(BigDecimal.ONE, buyOrder2.getCurrentQuantity());

        // Verify that the second buy is not removed from the order book
        assertEquals(1, orderBook.size());
        assertEquals(3, completedOrderBook.size());
    }

    @Test
    void testFIFOMatchOrders_MultipleOrdersQuantityReduced() {
        // Test when multiple buy and sell orders have their quantity reduced before getting exhausted

        Order buyOrder1 = createOrder(OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);
        Order buyOrder2 = createOrder(OrderType.BUY, BigDecimal.ONE, BigDecimal.TEN);
        Order sellOrder1 = createOrder(OrderType.SELL, BigDecimal.ONE, BigDecimal.TEN);
        Order sellOrder2 = createOrder(OrderType.SELL, BigDecimal.TEN, BigDecimal.TEN);

        Map<UUID, Order> orderBook = new ConcurrentHashMap<>();
        Map<UUID, Order> completedOrderBook = new HashMap<>();
        orderBook.put(buyOrder1.getOrderId(), buyOrder1);
        orderBook.put(buyOrder2.getOrderId(), buyOrder2);
        orderBook.put(sellOrder1.getOrderId(), sellOrder1);
        orderBook.put(sellOrder2.getOrderId(), sellOrder2);

        // Match only one quantity, so
        buyOrder1.decreaseQuantity(BigDecimal.ONE);
        sellOrder1.decreaseQuantity(BigDecimal.ONE);

        fifoMatchingStrategy.matchOrders(orderBook, completedOrderBook);

        // Verify that the first buy and sell orders are executed and removed from the order book
        assertEquals(OrderStatus.EXECUTED, buyOrder1.getOrderStatus());
        assertEquals(OrderStatus.EXECUTED, sellOrder1.getOrderStatus());
        assertEquals(2, orderBook.size()); // Only one order should remain (whichever was created first)

        // Verify that the second buy and sell orders are not executed
        assertEquals(OrderStatus.CREATED, buyOrder2.getOrderStatus());
        assertEquals(OrderStatus.CREATED, sellOrder2.getOrderStatus());

        // Verify that the second buy and sell orders are not removed from the order book
        assertEquals(2, orderBook.size());
        assertEquals(2, completedOrderBook.size());
    }
}