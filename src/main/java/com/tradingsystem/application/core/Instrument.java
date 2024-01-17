package com.tradingsystem.application.core;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Instrument {
    private String id;
    private String symbol;

    private Boolean isComposite;

    private HashSet<Instrument> underlying;

    private BigDecimal marketPrice;

    public Instrument(String id, String symbol, Boolean isComposite) {
        this.id = id;
        this.symbol = symbol;
        this.isComposite = isComposite;
        this.underlying = new HashSet<>();
    }

    public String getId() {
        return id;
    }

    public void setUnderlying(List<Instrument> underlying) {
        this.underlying.addAll(underlying);
    }

    public Boolean getIsComposite() {
        return isComposite;
    }

    public HashSet<Instrument> getUnderlying() {
        return underlying;
    }

    public String getSymbol() {
        return symbol;
    }

    public BigDecimal getMarketPrice() {
        return marketPrice;
    }


    public void updateMarketPrice(BigDecimal newPrice) {
        this.marketPrice = newPrice;
    }

    /**
     * To check if two instruments are the same, I am checking the id and the underlyings
     * @param obj
     * @return boolean
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Instrument that = (Instrument) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(underlying, that.underlying);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, underlying);
    }

    @Override
    public String toString() {
        String underlyingString = underlying.stream()
                .map(Instrument::getId)
                .collect(Collectors.joining(", "));

        return "Id: " + id + " isComposite: " + isComposite + " symbol: " + symbol +  " price: " + marketPrice  + " underlying: [" + underlyingString + "]";
    }

}
