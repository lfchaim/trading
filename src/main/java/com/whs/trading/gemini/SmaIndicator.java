package com.whs.trading.gemini;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class SmaIndicator implements TechnicalIndicator {

    private static final String NAME = "SMA";
    private static final int DEFAULT_PERIOD = 20; // Período comum para SMA
    private static final int SCALE = 2; // Escala para o valor da SMA

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int period = params.containsKey("smaPeriod") ? Integer.parseInt(params.get("smaPeriod")) : DEFAULT_PERIOD;

        if (historicalData == null || historicalData.size() < period) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para calcular SMA com período " + period);
            return result;
        }

        // Pega os 'period' últimos preços de fechamento
        List<CandlestickData> relevantData = historicalData.subList(historicalData.size() - period, historicalData.size());
        BigDecimal sumOfCloses = relevantData.stream()
                                     .map(CandlestickData::getClose)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal smaValue = sumOfCloses.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        BigDecimal currentPrice = historicalData.get(historicalData.size() - 1).getClose();

        result.addValue("smaValue", smaValue);
        result.addValue("currentPrice", currentPrice);
        result.addValue("period", period);

        // Sinal simples: preço atual vs SMA
        // (Estratégias mais complexas envolvem cruzamentos de SMAs de diferentes períodos)
        if (currentPrice.compareTo(smaValue) > 0) {
            result.setSignal(Signal.BUY); // Ou HOLD se já comprado
            result.setDetails(String.format("Preço (%.2f) acima da SMA(%d) (%.2f). Tendência de alta.", currentPrice, period, smaValue));
        } else if (currentPrice.compareTo(smaValue) < 0) {
            result.setSignal(Signal.SELL); // Ou HOLD se já vendido
            result.setDetails(String.format("Preço (%.2f) abaixo da SMA(%d) (%.2f). Tendência de baixa.", currentPrice, period, smaValue));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Preço (%.2f) igual à SMA(%d) (%.2f).", currentPrice, period, smaValue));
        }
        return result;
    }
}