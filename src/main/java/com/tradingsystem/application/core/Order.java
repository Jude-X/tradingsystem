package com.tradingsystem.application.core;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public class Order {
    private UUID orderId = UUID.randomUUID();

    private Instrument instrument;
    private String traderId;
    private OrderType orderType;
    private BigDecimal price;
    private BigDecimal initialQuantity;

    private BigDecimal currentQuantity;

    private OrderStatus orderStatus = OrderStatus.CREATED;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdTimestamp = Instant.now();

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant executedTimestamp;

    public Order(String traderId, Instrument instrument, OrderType orderType, BigDecimal price, BigDecimal quantity) {
        this.instrument = instrument;
        this.traderId = traderId;
        this.orderType = orderType;
        this.price = price;
        this.initialQuantity = quantity;
        this.currentQuantity = quantity;
    }

    public UUID getOrderId() {
        return orderId;
    }

    public String getTraderId() {
        return traderId;
    }

    public OrderType getOrderType() {
        return orderType;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public BigDecimal getCurrentQuantity() {
        return currentQuantity;
    }

    public Instant getCreatedTimestamp() {
        return createdTimestamp;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public BigDecimal decreaseQuantity(BigDecimal deltaQty){
        currentQuantity = currentQuantity.subtract(deltaQty);
        return currentQuantity;
    }

    public void setCurrentQuantity(BigDecimal currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void setExecutedTimestamp(Instant executedTimestamp) {
        this.executedTimestamp = executedTimestamp;
    }
}
