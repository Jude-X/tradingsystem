package com.tradingsystem.application.unit;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.core.OrderType;

import java.math.BigDecimal;

public class BaseUnitTest {
    // Helper method to create an order
    Order createOrder(OrderType orderType, BigDecimal price, BigDecimal quantity) {
        Instrument instrument = new Instrument("instrumentId", "symbolId", false);
        instrument.updateMarketPrice(price);

        return new Order(
                "traderId", instrument, orderType, price, quantity);
    }
}
