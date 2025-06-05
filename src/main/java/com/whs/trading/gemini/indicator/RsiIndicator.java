package com.whs.trading.gemini.indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service; // Importa a anotação Service

import com.whs.trading.gemini.dto.CandlestickData;
import com.whs.trading.gemini.dto.IndicatorResult;
import com.whs.trading.gemini.dto.Signal;

@Service // Para que o Spring gerencie este bean
public class RsiIndicator implements TechnicalIndicator {

    private static final String NAME = "RSI";
    private static final int DEFAULT_PERIOD = 14;
    private static final int SCALE = 4; // Escala para precisão do BigDecimal

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int period = params.containsKey("rsiPeriod") ? Integer.parseInt(params.get("rsiPeriod")) : DEFAULT_PERIOD;

        if (historicalData == null || historicalData.size() < period + 1) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para calcular o RSI com período " + period);
            return result;
        }

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        // Calcula as variações de preço (ganhos e perdas)
        for (int i = 1; i < historicalData.size(); i++) {
            BigDecimal difference = historicalData.get(i).getClose().subtract(historicalData.get(i - 1).getClose());
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                gains.add(difference);
                losses.add(BigDecimal.ZERO);
            } else {
                gains.add(BigDecimal.ZERO);
                losses.add(difference.abs());
            }
        }
        
        if (gains.size() < period) { // Precisamos de 'period' ganhos/perdas para a primeira média
             result.setSignal(Signal.NEUTRAL);
             result.setDetails("Dados de ganhos/perdas insuficientes após cálculo de diferenças para o período " + period);
             return result;
        }

        // Calcula a média dos ganhos e perdas para o primeiro período
        BigDecimal avgGain = gains.subList(0, period).stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        BigDecimal avgLoss = losses.subList(0, period).stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);

        // Suavização para os períodos subsequentes (Wilder's smoothing)
        for (int i = period; i < gains.size(); i++) {
            avgGain = (avgGain.multiply(BigDecimal.valueOf(period - 1)).add(gains.get(i))).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
            avgLoss = (avgLoss.multiply(BigDecimal.valueOf(period - 1)).add(losses.get(i))).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            // Evita divisão por zero; se não há perdas, RSI é 100 (extremamente forte)
            result.addValue("rsiValue", BigDecimal.valueOf(100));
            result.setSignal(Signal.SELL); // Condição de sobrecompra extrema
            result.setDetails("RSI: 100.00 (Sobrecompra Extrema - Ausência de Perdas Médias)");
            return result;
        }
        
        BigDecimal rs = avgGain.divide(avgLoss, SCALE, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), SCALE, RoundingMode.HALF_UP));

        result.addValue("rsiValue", rsi.setScale(2, RoundingMode.HALF_UP));
        result.addValue("period", period);

        // Define o sinal baseado nos níveis de sobrecompra/sobrevenda
        if (rsi.compareTo(BigDecimal.valueOf(70)) > 0) {
            result.setSignal(Signal.SELL);
            result.setDetails(String.format("RSI: %.2f (Sobrecompra)", rsi));
        } else if (rsi.compareTo(BigDecimal.valueOf(30)) < 0) {
            result.setSignal(Signal.BUY);
            result.setDetails(String.format("RSI: %.2f (Sobrevenda)", rsi));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("RSI: %.2f (Neutra)", rsi));
        }
        return result;
    }
}