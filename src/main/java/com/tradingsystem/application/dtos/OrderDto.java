package com.tradingsystem.application.dtos;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.core.OrderType;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderDto {

    @NotNull(message = "instrumentId cannot be null")
    private String instrumentId;

    @NotNull(message = "traderId cannot be null")
    private String traderId;

    @NotNull(message = "orderType cannot be null")
    private OrderType orderType;

    @NotNull(message = "price cannot be null")
    private BigDecimal price;

    @NotNull(message = "quantity cannot be null")
    private BigDecimal quantity;

    public Order toOrder(Instrument instrument){
        return new Order(traderId, instrument, orderType, price, quantity);
    }

    public String getInstrumentId() {
        return instrumentId;
    }

    public void setInstrumentId(String instrumentId) {
        this.instrumentId = instrumentId;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public void setOrderType(OrderType orderType) {
        this.orderType = orderType;
    }
}
