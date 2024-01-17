package com.tradingsystem.application.integration;

import com.tradingsystem.application.services.ExchangeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.Mockito.doNothing;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private ExchangeService exchangeService;

    @Test
    void setMatchingStrategy_ValidStrategy_ShouldReturnOk() throws Exception {
        // Arrange
        doNothing().when(exchangeService).setStrategy(Mockito.any());

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/strategy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"strategy\": \"ProRata\"}"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Matching strategy set to ProRata"));
    }

    @Test
    void setMatchingStrategy_InvalidStrategy_ShouldReturnBadRequest() throws Exception {
        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/admin/strategy")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"strategy\": \"InvalidStrategy\"}"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
