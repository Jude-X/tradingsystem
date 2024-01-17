package com.tradingsystem.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.core.Order;
import com.tradingsystem.application.core.OrderType;
import com.tradingsystem.application.dtos.OrderDto;
import com.tradingsystem.application.services.InstrumentService;
import com.tradingsystem.application.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstrumentService instrumentService;

    @MockBean
    private OrderService orderService;

    @Test
    void placeOrder_ValidOrder_ShouldReturnCreated() throws Exception {
        // Arrange
        Instrument instrument = new Instrument("instrumentId123", "symbol123", false);
        OrderDto orderDto = new OrderDto();
        orderDto.setTraderId("trader1");
        orderDto.setOrderType(OrderType.BUY);
        orderDto.setInstrumentId(instrument.getId());
        orderDto.setPrice(BigDecimal.TEN);
        orderDto.setQuantity(BigDecimal.TEN);

        // Mock the service behavior
        when(instrumentService.getInstrumentById(instrument.getId())).thenReturn(instrument);
        doNothing().when(orderService).placeOrder(orderDto.toOrder(instrument));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(orderDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Order placed successfully: ")));

    }

    @Test
    void listOrders_ShouldReturnListOfOrders() throws Exception {
        // Arrange
        // Mock the service behavior
        Instrument instrument = new Instrument("instrumentId123", "symbol123", false);
        List<Order> orders = new ArrayList<>();
        orders.add(new Order("trader123", instrument, OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN));
        when(orderService.getOrders()).thenReturn(orders);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.size()").value(1)); // Assuming there is one order in the mocked response
    }

    @Test
    void getOrder_ExistingOrderId_ShouldReturnOrder() throws Exception {
        // Arrange
        UUID orderId = UUID.randomUUID();
        Instrument instrument = new Instrument("instrumentId123", "symbol123", false);
        Order order = new Order("trader123", instrument, OrderType.BUY, BigDecimal.TEN, BigDecimal.TEN);

        // Mock the service behavior
        when(orderService.getOrderById(orderId)).thenReturn(order);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{orderId}", orderId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.traderId").value(order.getTraderId()))
                .andExpect(jsonPath("$.orderType").value(order.getOrderType().toString()))
                .andExpect(jsonPath("$.price").value(order.getPrice().toString()))
                .andExpect(jsonPath("$.currentQuantity").value(order.getCurrentQuantity().toString()));
    }

    @Test
    void getOrder_NonExistingOrderId_ShouldReturnNotFound() throws Exception {
        // Arrange
        UUID nonExistingOrderId = UUID.randomUUID();

        // Mock the service behavior (returning null for a non-existing order)
        when(orderService.getOrderById(nonExistingOrderId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/orders/{orderId}", nonExistingOrderId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
