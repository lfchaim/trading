package com.whs.trading.gemini.dto;

import java.util.HashMap;
import java.util.Map;

public class IndicatorResult {
    private String indicatorName;
    private Signal signal;
    private Map<String, Object> values; // Para armazenar valores calculados (ex: valor do RSI, níveis de S/R)
    private String details; // Descrição textual do resultado

    public IndicatorResult(String indicatorName) {
        this.indicatorName = indicatorName;
        this.values = new HashMap<>();
    }

    // Getters e Setters
    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String indicatorName) { this.indicatorName = indicatorName; }
    public Signal getSignal() { return signal; }
    public void setSignal(Signal signal) { this.signal = signal; }
    public Map<String, Object> getValues() { return values; }
    public void setValues(Map<String, Object> values) { this.values = values; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public void addValue(String key, Object value) {
        this.values.put(key, value);
    }
}