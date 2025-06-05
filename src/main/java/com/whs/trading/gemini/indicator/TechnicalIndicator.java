package com.whs.trading.gemini.indicator;

import java.util.List;
import java.util.Map;

import com.whs.trading.gemini.dto.CandlestickData;
import com.whs.trading.gemini.dto.IndicatorResult;

public interface TechnicalIndicator {
    String getName(); // Nome do indicador (ex: "RSI", "SMA")
    IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params);
    // 'params' pode conter configurações como período do RSI, períodos das MAs, etc.
    // A lista historicalData deve estar ordenada do mais antigo para o mais recente.
}