package com.tradingsystem.application.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradingsystem.application.algorithms.FIFOMatchingStrategy;
import com.tradingsystem.application.algorithms.MatchingStrategy;
import com.tradingsystem.application.algorithms.ProRataMatchingStrategy;
import com.tradingsystem.application.core.MatchingStrategyType;
import com.tradingsystem.application.services.ExchangeService;
import com.tradingsystem.application.services.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Arrays;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ExchangeService exchangeService;
    private final InstrumentService instrumentService;

    @Autowired
    public AdminController(ExchangeService exchangeService, InstrumentService instrumentService) {
        this.exchangeService = exchangeService;
        this.instrumentService = instrumentService;
    }

    @PostMapping("/strategy")
    public ResponseEntity<String> setMatchingStrategy(@RequestBody StrategyBodyDto body) {
        try {
            MatchingStrategy matchingStrategy = createMatchingStrategy(body.strategy);
            if(matchingStrategy.getClass().equals(exchangeService.getStrategy().getClass()))
                return new ResponseEntity<>("This strategy is already set", HttpStatus.BAD_REQUEST);
            exchangeService.setStrategy(matchingStrategy);
            return new ResponseEntity<>("Matching strategy set to " + body.strategy, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            String validOptions = Arrays.stream(MatchingStrategyType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));
            return new ResponseEntity<>("Invalid matching strategy. Valid options: " + validOptions, HttpStatus.BAD_REQUEST);
        }
    }

    private MatchingStrategy createMatchingStrategy(MatchingStrategyType strategyType) {
        switch (strategyType) {
            case FIFO:
                return new FIFOMatchingStrategy(instrumentService);
            case ProRata:
                return new ProRataMatchingStrategy(instrumentService);
            default:
                throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
    }
}


class StrategyBodyDto {
    @JsonProperty("strategy")
    MatchingStrategyType strategy;
}
