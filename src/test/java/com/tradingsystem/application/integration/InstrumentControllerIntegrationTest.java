package com.tradingsystem.application.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradingsystem.application.api.InstrumentController;
import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.dtos.InstrumentDto;
import com.tradingsystem.application.services.InstrumentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;

import static org.mockito.Mockito.*;

@WebMvcTest(InstrumentController.class)
@AutoConfigureMockMvc
class InstrumentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InstrumentService instrumentService;

    @Test
    void addInstrument_ValidInstrument_ShouldReturnCreated() throws Exception {
        // Arrange
        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setId("instrumentId123");
        instrumentDto.setSymbol("symbol123");
        instrumentDto.setIsComposite(true);

        doNothing().when(instrumentService).addInstrument(Mockito.any(Instrument.class));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instrumentDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().string("Instrument added successfully"));
    }

    @Test
    void addInstrument_InstrumentAlreadyExists_ShouldReturnBadRequest() throws Exception {
        // Arrange
        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setId("instrumentId123");
        instrumentDto.setSymbol("symbol123");
        instrumentDto.setIsComposite(true);

        when(instrumentService.getInstrumentById(instrumentDto.getId())).thenReturn(new Instrument("id", "symbol", false));

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instrumentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Instrument with ID instrumentId123 already exists"));
    }

    @Test
    void addInstrument_InvalidUnderlyingInstrument_ShouldReturnBadRequest() throws Exception {
        // Arrange
        InstrumentDto instrumentDto = new InstrumentDto();
        instrumentDto.setId("instrumentId123");
        instrumentDto.setSymbol("symbol123");
        instrumentDto.setIsComposite(true);
        instrumentDto.setUnderlying(Collections.singletonList("invalidInstrumentId"));

        when(instrumentService.getInstrumentById(instrumentDto.getId())).thenReturn(null);
        when(instrumentService.getInstrumentById("invalidInstrumentId")).thenReturn(null);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.post("/api/instruments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(instrumentDto)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Instrument with ID invalidInstrumentId does not exist"));
    }

    @Test
    void getInstrument_ExistingInstrumentId_ShouldReturnInstrument() throws Exception {
        // Arrange
        String existingInstrumentId = "instrumentId123";
        Instrument existingInstrument = new Instrument(existingInstrumentId, "symbol123", true);

        when(instrumentService.getInstrumentById(existingInstrumentId)).thenReturn(existingInstrument);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/instruments/{instrumentId}", existingInstrumentId))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingInstrumentId))
                .andExpect(MockMvcResultMatchers.jsonPath("$.symbol").value("symbol123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.isComposite").value(true));
    }

    @Test
    void getInstrument_NonExistingInstrumentId_ShouldReturnNotFound() throws Exception {
        // Arrange
        String nonExistingInstrumentId = "nonExistingInstrumentId";
        when(instrumentService.getInstrumentById(nonExistingInstrumentId)).thenReturn(null);

        // Act and Assert
        mockMvc.perform(MockMvcRequestBuilders.get("/api/instruments/{instrumentId}", nonExistingInstrumentId))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
