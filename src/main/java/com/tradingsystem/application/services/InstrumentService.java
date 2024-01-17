package com.tradingsystem.application.services;

import com.tradingsystem.application.core.Instrument;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class InstrumentService {

    private Map<String, Instrument> instruments;
    private boolean increasePriceFlag;

    public InstrumentService() {
        this.instruments = new HashMap<>();
        this.increasePriceFlag = true;
    }

    public void addInstrument(Instrument instrument) {
        instruments.put(instrument.getId(), instrument);
    }

    public Instrument getUpdatedPriceOfInstrument(Instrument instrument) {
        return instrument;
    }

    public Instrument getInstrumentById(String instrumentId) {
        return instruments.get(instrumentId);
    }

    public Map<String, Instrument> getInstruments() {
        return instruments;
    }

    // Simulating a websocket connection to the candle/quote service
    @Scheduled(fixedRate = 1000)
    private void updateMarketPrices() {
        for (Instrument instrument : instruments.values()) {
            BigDecimal currentPrice = instrument.getMarketPrice();
            if(currentPrice == null) currentPrice = new BigDecimal(new Random().nextDouble() * 100); // Adjust the range as needed

            if (increasePriceFlag) {
                instrument.updateMarketPrice(currentPrice.add(BigDecimal.ONE));
            } else {
                instrument.updateMarketPrice(currentPrice.subtract(BigDecimal.ONE));
            }
        }
        increasePriceFlag = !increasePriceFlag;
    }
}
