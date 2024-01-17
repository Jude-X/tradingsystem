package com.tradingsystem.application.api;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.dtos.OrderDto;
import com.tradingsystem.application.services.InstrumentService;
import com.tradingsystem.application.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final InstrumentService instrumentService;

    @Autowired
    public OrderController(OrderService orderService, InstrumentService instrumentService) {
        this.orderService = orderService;
        this.instrumentService = instrumentService;
    }

    @PostMapping("")
    public ResponseEntity<String> placeOrder(@RequestBody OrderDto orderDto) {
        Instrument instrument = instrumentService.getInstrumentById(orderDto.getInstrumentId());
        if(instrument == null){
            return new ResponseEntity<>("Instrument does not exist", HttpStatus.BAD_REQUEST);
        }
        Order order = orderDto.toOrder(instrument);
        orderService.placeOrder(order);
        return new ResponseEntity<>("Order placed successfully: " + order.getOrderId(), HttpStatus.CREATED);
    }

    @GetMapping("")
    public ResponseEntity<List<Order>> listOrders() {
        List<Order> orders = new ArrayList<>();
        orders.addAll(orderService.getOrders());
        orders.addAll(orderService.getCompletedOrders());
        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrderById(UUID.fromString(orderId));

        if (order != null) {
            return new ResponseEntity<>(order, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
