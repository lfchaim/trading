package com.whs.trading.gemini.indicator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.whs.trading.gemini.dto.CandlestickData;
import com.whs.trading.gemini.dto.IndicatorResult;
import com.whs.trading.gemini.dto.Signal;

@Service
public class MacdIndicator implements TechnicalIndicator {

    private static final String NAME = "MACD";
    private static final int DEFAULT_SHORT_PERIOD = 12;
    private static final int DEFAULT_LONG_PERIOD = 26;
    private static final int DEFAULT_SIGNAL_PERIOD = 9;
    private static final int SCALE = 4; // Maior precisão para cálculos intermediários

    @Override
    public String getName() {
        return NAME;
    }

    private List<BigDecimal> calculateEMA(List<BigDecimal> prices, int period) {
        if (prices == null || prices.size() < period) {
            return Collections.emptyList();
        }

        List<BigDecimal> emaList = new ArrayList<>();
        BigDecimal multiplier = BigDecimal.valueOf(2.0 / (period + 1));

        // Primeira EMA é a SMA do período
        BigDecimal sumForSma = BigDecimal.ZERO;
        for (int i = 0; i < period; i++) {
            sumForSma = sumForSma.add(prices.get(i));
        }
        BigDecimal previousEma = sumForSma.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        emaList.add(previousEma); // Adiciona a primeira EMA calculada (referente ao final do primeiro 'period')

        // Calcula EMAs subsequentes
        for (int i = period; i < prices.size(); i++) {
            // EMA = (Close - EMA_prev) * multiplier + EMA_prev
            BigDecimal currentPrice = prices.get(i);
            BigDecimal ema = currentPrice.subtract(previousEma).multiply(multiplier).add(previousEma);
            emaList.add(ema.setScale(SCALE, RoundingMode.HALF_UP));
            previousEma = ema;
        }
        return emaList; // Esta lista terá (prices.size() - period + 1) elementos
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());

        int shortPeriod = params.containsKey("macdShortPeriod") ? Integer.parseInt(params.get("macdShortPeriod")) : DEFAULT_SHORT_PERIOD;
        int longPeriod = params.containsKey("macdLongPeriod") ? Integer.parseInt(params.get("macdLongPeriod")) : DEFAULT_LONG_PERIOD;
        int signalPeriod = params.containsKey("macdSignalPeriod") ? Integer.parseInt(params.get("macdSignalPeriod")) : DEFAULT_SIGNAL_PERIOD;

        // Validação de dados suficientes
        // Para calcular EMA longa, precisamos de 'longPeriod' pontos.
        // Para calcular a linha de sinal, precisamos de 'signalPeriod' pontos da linha MACD.
        // A linha MACD começa a ser gerada após 'longPeriod' pontos.
        // Portanto, precisamos de pelo menos 'longPeriod + signalPeriod -1' para ter uma linha de sinal.
        if (historicalData == null || historicalData.size() < longPeriod + signalPeriod) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Dados históricos insuficientes. Necessário: %d, Disponível: %d",
                                            longPeriod + signalPeriod, historicalData.size()));
            return result;
        }

        List<BigDecimal> closePrices = historicalData.stream()
                                           .map(CandlestickData::getClose)
                                           .collect(Collectors.toList());

        List<BigDecimal> emaShortList = calculateEMA(closePrices, shortPeriod);
        List<BigDecimal> emaLongList = calculateEMA(closePrices, longPeriod);

        // Alinhar as listas de EMA. A EMA mais curta terá mais valores no início.
        // Precisamos dos últimos valores onde ambas existem.
        // A emaLongList tem (closePrices.size() - longPeriod + 1) elementos.
        // A emaShortList tem (closePrices.size() - shortPeriod + 1) elementos.
        // A diferença de tamanho é (longPeriod - shortPeriod).
        // Pegamos a sublista da emaShortList para alinhá-la com a emaLongList.
        int diff = longPeriod - shortPeriod;
        if (diff < 0 || emaShortList.size() <= diff || emaLongList.isEmpty()) { // Checagem de segurança
             result.setSignal(Signal.NEUTRAL);
             result.setDetails("Erro ao alinhar EMAs para cálculo do MACD.");
             return result;
        }
        List<BigDecimal> alignedEmaShort = emaShortList.subList(diff, emaShortList.size());
        
        List<BigDecimal> macdLine = new ArrayList<>();
        // Ambas as listas (alignedEmaShort e emaLongList) agora têm o mesmo tamanho
        // e correspondem aos mesmos pontos no tempo (a partir do 'longPeriod'-ésimo ponto de 'closePrices').
        for (int i = 0; i < emaLongList.size(); i++) {
            macdLine.add(alignedEmaShort.get(i).subtract(emaLongList.get(i)).setScale(SCALE, RoundingMode.HALF_UP));
        }

        if (macdLine.size() < signalPeriod) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Linha MACD com dados insuficientes para calcular a Linha de Sinal.");
            return result;
        }

        List<BigDecimal> signalLineList = calculateEMA(macdLine, signalPeriod);
        
        // Pegar os últimos valores calculados
        BigDecimal lastMacdValue = macdLine.get(macdLine.size() - 1);
        BigDecimal prevMacdValue = macdLine.size() > 1 ? macdLine.get(macdLine.size() - 2) : null;
        
        BigDecimal lastSignalValue = signalLineList.get(signalLineList.size() - 1);
        BigDecimal prevSignalValue = signalLineList.size() > 1 ? signalLineList.get(signalLineList.size() - 2) : null;

        BigDecimal histogram = lastMacdValue.subtract(lastSignalValue).setScale(SCALE, RoundingMode.HALF_UP);

        result.addValue("macdLine", lastMacdValue.setScale(2, RoundingMode.HALF_UP));
        result.addValue("signalLine", lastSignalValue.setScale(2, RoundingMode.HALF_UP));
        result.addValue("histogram", histogram.setScale(2, RoundingMode.HALF_UP));
        result.addValue("shortPeriod", shortPeriod);
        result.addValue("longPeriod", longPeriod);
        result.addValue("signalPeriod", signalPeriod);

        // Lógica de Sinal (baseada no cruzamento mais recente)
        String signalDetails = String.format("MACD(%.2f), Sinal(%.2f), Hist(%.2f).",
                                            lastMacdValue, lastSignalValue, histogram);

        if (prevMacdValue != null && prevSignalValue != null) {
            boolean crossedUp = prevMacdValue.compareTo(prevSignalValue) <= 0 && lastMacdValue.compareTo(lastSignalValue) > 0;
            boolean crossedDown = prevMacdValue.compareTo(prevSignalValue) >= 0 && lastMacdValue.compareTo(lastSignalValue) < 0;

            if (crossedUp) {
                result.setSignal(Signal.BUY);
                signalDetails += " Cruzamento de COMPRA da linha MACD sobre a linha de Sinal.";
            } else if (crossedDown) {
                result.setSignal(Signal.SELL);
                signalDetails += " Cruzamento de VENDA da linha MACD sob a linha de Sinal.";
            } else {
                // Sem cruzamento, podemos verificar a posição em relação à linha zero ou tendência do histograma
                if (lastMacdValue.compareTo(BigDecimal.ZERO) > 0 && lastSignalValue.compareTo(BigDecimal.ZERO) > 0) {
                    result.setSignal(Signal.HOLD); // Se já comprado, ou otimista
                    signalDetails += " Ambas as linhas acima de zero (Otimista).";
                } else if (lastMacdValue.compareTo(BigDecimal.ZERO) < 0 && lastSignalValue.compareTo(BigDecimal.ZERO) < 0) {
                    result.setSignal(Signal.HOLD); // Se já vendido, ou pessimista
                    signalDetails += " Ambas as linhas abaixo de zero (Pessimista).";
                } else {
                     result.setSignal(Signal.NEUTRAL);
                     signalDetails += " Neutro.";
                }
            }
        } else {
            result.setSignal(Signal.NEUTRAL);
            signalDetails += " Dados insuficientes para detectar cruzamento.";
        }
        result.setDetails(signalDetails);
        return result;
    }
}