package com.tradingsystem.application.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Collections;
import java.util.List;

public class InstrumentDto {

    @NotBlank(message = "ID cannot be blank")
    private String id;

    @NotBlank(message = "Symbol cannot be blank")
    private String symbol;

    @NotNull(message = "isComposite cannot be null")
    private Boolean isComposite;

    private List<String> underlying = Collections.emptyList();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public Boolean getIsComposite() {
        return isComposite;
    }

    public void setIsComposite(Boolean isComposite) {
        this.isComposite = isComposite;
    }

    public List<String> getUnderlying() {
        return underlying;
    }

    public void setUnderlying(List<String> underlying) {
        this.underlying = underlying;
    }
}




