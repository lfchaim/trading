package com.whs.trading.gemini.indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whs.trading.gemini.dto.CandlestickData;
import com.whs.trading.gemini.dto.IndicatorResult;
import com.whs.trading.gemini.dto.Signal;

@Service
public class SupportResistanceIndicator implements TechnicalIndicator {

    private static final String NAME = "Support/Resistance";
    private static final int DEFAULT_LOOKBACK_PERIOD = 20; // Nº de velas para identificar S/R
    // Distância percentual para considerar "próximo" ao suporte/resistência
    private static final BigDecimal PROXIMITY_PERCENTAGE = new BigDecimal("0.01"); // 1%

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int lookbackPeriod = params.containsKey("srLookbackPeriod") ? Integer.parseInt(params.get("srLookbackPeriod")) : DEFAULT_LOOKBACK_PERIOD;

        if (historicalData == null || historicalData.size() < lookbackPeriod) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para S/R com lookback " + lookbackPeriod);
            return result;
        }

        List<CandlestickData> relevantData = historicalData.subList(historicalData.size() - lookbackPeriod, historicalData.size());

        BigDecimal supportLevel = Collections.min(relevantData.stream().map(CandlestickData::getLow).collect(Collectors.toList()));
        BigDecimal resistanceLevel = Collections.max(relevantData.stream().map(CandlestickData::getHigh).collect(Collectors.toList()));
        BigDecimal currentPrice = historicalData.get(historicalData.size() - 1).getClose();

        result.addValue("support", supportLevel.setScale(2, RoundingMode.HALF_UP));
        result.addValue("resistance", resistanceLevel.setScale(2, RoundingMode.HALF_UP));
        result.addValue("currentPrice", currentPrice.setScale(2, RoundingMode.HALF_UP));
        result.addValue("lookbackPeriod", lookbackPeriod);

        BigDecimal proximityToSupport = currentPrice.subtract(supportLevel);
        BigDecimal proximityToResistance = resistanceLevel.subtract(currentPrice);

        // Verifica se o preço está próximo ao suporte (dentro de PROXIMITY_PERCENTAGE acima do suporte)
        if (proximityToSupport.compareTo(BigDecimal.ZERO) >= 0 && 
            proximityToSupport.compareTo(supportLevel.multiply(PROXIMITY_PERCENTAGE)) <= 0) {
            result.setSignal(Signal.BUY);
            result.setDetails(String.format("Preço (%.2f) próximo ao Suporte (%.2f). Potencial Compra.", currentPrice, supportLevel));
        } 
        // Verifica se o preço está próximo à resistência (dentro de PROXIMITY_PERCENTAGE abaixo da resistência)
        else if (proximityToResistance.compareTo(BigDecimal.ZERO) >= 0 &&
                   proximityToResistance.compareTo(resistanceLevel.multiply(PROXIMITY_PERCENTAGE)) <= 0) {
            result.setSignal(Signal.SELL);
            result.setDetails(String.format("Preço (%.2f) próximo à Resistência (%.2f). Potencial Venda.", currentPrice, resistanceLevel));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Preço (%.2f) entre Suporte (%.2f) e Resistência (%.2f).", currentPrice, supportLevel, resistanceLevel));
        }
        return result;
    }
}