package com.tradingsystem.application.api;

import com.tradingsystem.application.core.Instrument;
import com.tradingsystem.application.dtos.InstrumentDto;
import com.tradingsystem.application.services.InstrumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/instruments")
public class InstrumentController {

    private final InstrumentService instrumentService;

    @Autowired
    public InstrumentController(InstrumentService instrumentService) {
        this.instrumentService = instrumentService;
    }

    @PostMapping("")
    public ResponseEntity<String> addInstrument(@RequestBody InstrumentDto instrumentDto) {
        List<Instrument> underlying = new ArrayList<>();

        // Check if the instrument already exists
        if (instrumentService.getInstrumentById(instrumentDto.getId()) != null) {
            return new ResponseEntity<>("Instrument with ID " + instrumentDto.getId() + " already exists", HttpStatus.BAD_REQUEST);
        }

        //Check that each underlying is valid
        if(instrumentDto.getIsComposite()){
            for(String instrumentId : instrumentDto.getUnderlying()){
                if (instrumentService.getInstrumentById(instrumentId) == null) {
                    return new ResponseEntity<>("Instrument with ID " + instrumentId + " does not exist", HttpStatus.BAD_REQUEST);
                }
                underlying.add(instrumentService.getInstrumentById(instrumentId));
            }
        }


        Instrument instrument = new Instrument(instrumentDto.getId(), instrumentDto.getSymbol(), instrumentDto.getIsComposite());
        instrument.setUnderlying(underlying);

        // Instrument doesn't exist, add it
        instrumentService.addInstrument(instrument);
        return new ResponseEntity<>("Instrument added successfully", HttpStatus.CREATED);
    }


    @GetMapping("/{instrumentId}")
    public ResponseEntity<Instrument> getInstrument(@PathVariable String instrumentId) {
        Instrument instrument = instrumentService.getInstrumentById(instrumentId);
        if (instrument != null) {
            return new ResponseEntity<>(instrument, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
