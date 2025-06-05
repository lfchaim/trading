package com.whs.trading.gemini;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class BollingerBandsIndicator implements TechnicalIndicator {

    private static final String NAME = "Bollinger Bands";
    private static final int DEFAULT_PERIOD = 20;
    private static final BigDecimal DEFAULT_STD_DEV_MULTIPLIER = new BigDecimal("2.0");
    private static final int SCALE = 2; // Para valores finais
    private static final int CALC_SCALE = 8; // Para cálculos intermediários de desvio padrão

    @Override
    public String getName() {
        return NAME;
    }

    private BigDecimal calculateStandardDeviation(List<BigDecimal> prices, BigDecimal mean) {
        if (prices == null || prices.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal sumOfSquaredDifferences = BigDecimal.ZERO;
        for (BigDecimal price : prices) {
            BigDecimal difference = price.subtract(mean);
            sumOfSquaredDifferences = sumOfSquaredDifferences.add(difference.multiply(difference));
        }
        BigDecimal variance = sumOfSquaredDifferences.divide(BigDecimal.valueOf(prices.size()), CALC_SCALE, RoundingMode.HALF_UP);
        // BigDecimal.sqrt() requer Java 9+ com MathContext para precisão
        // Se estiver usando Java 8, pode precisar de uma biblioteca externa ou implementação própria para sqrt.
        // Exemplo para Java 9+:
        // return variance.sqrt(new MathContext(CALC_SCALE, RoundingMode.HALF_UP));
        // Para compatibilidade mais ampla ou se não tiver Java 9+, use double e converta:
        double varianceDouble = variance.doubleValue();
        return BigDecimal.valueOf(Math.sqrt(varianceDouble)).setScale(CALC_SCALE, RoundingMode.HALF_UP);

    }


    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());

        int period = params.containsKey("bbPeriod") ? Integer.parseInt(params.get("bbPeriod")) : DEFAULT_PERIOD;
        BigDecimal stdDevMultiplier = params.containsKey("bbStdDevMult") ?
                                      new BigDecimal(params.get("bbStdDevMult")) : DEFAULT_STD_DEV_MULTIPLIER;

        if (historicalData == null || historicalData.size() < period) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Dados históricos insuficientes. Necessário: %d, Disponível: %d",
                                            period, historicalData.size()));
            return result;
        }

        List<CandlestickData> relevantData = historicalData.subList(historicalData.size() - period, historicalData.size());
        List<BigDecimal> closePrices = relevantData.stream()
                                         .map(CandlestickData::getClose)
                                         .collect(Collectors.toList());

        // Calcular Banda Média (SMA)
        BigDecimal sumOfCloses = closePrices.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal middleBand = sumOfCloses.divide(BigDecimal.valueOf(period), CALC_SCALE, RoundingMode.HALF_UP);

        // Calcular Desvio Padrão
        BigDecimal standardDeviation = calculateStandardDeviation(closePrices, middleBand);

        // Calcular Bandas Superior e Inferior
        BigDecimal stdDevOffset = standardDeviation.multiply(stdDevMultiplier);
        BigDecimal upperBand = middleBand.add(stdDevOffset);
        BigDecimal lowerBand = middleBand.subtract(stdDevOffset);

        BigDecimal currentPrice = historicalData.get(historicalData.size() - 1).getClose();

        result.addValue("middleBand", middleBand.setScale(SCALE, RoundingMode.HALF_UP));
        result.addValue("upperBand", upperBand.setScale(SCALE, RoundingMode.HALF_UP));
        result.addValue("lowerBand", lowerBand.setScale(SCALE, RoundingMode.HALF_UP));
        result.addValue("currentPrice", currentPrice.setScale(SCALE, RoundingMode.HALF_UP));
        result.addValue("period", period);
        result.addValue("stdDevMultiplier", stdDevMultiplier);

        // Lógica de Sinal
        String signalDetails = String.format("Preço: %.2f, Inf: %.2f, Méd: %.2f, Sup: %.2f.",
                                            currentPrice, lowerBand, middleBand, upperBand);

        // %B = (Preço - Banda Inferior) / (Banda Superior - Banda Inferior)
        // Ajuda a quantificar a posição do preço em relação às bandas.
        BigDecimal bandWidth = upperBand.subtract(lowerBand);
        BigDecimal percentB = BigDecimal.ZERO; // Default if bandwidth is zero
        if (bandWidth.compareTo(BigDecimal.ZERO) != 0) {
             percentB = currentPrice.subtract(lowerBand)
                                 .divide(bandWidth, CALC_SCALE, RoundingMode.HALF_UP)
                                 .multiply(BigDecimal.valueOf(100)); // como porcentagem
            result.addValue("percentB", percentB.setScale(SCALE, RoundingMode.HALF_UP));
        } else {
            result.addValue("percentB", "N/A (Bandwidth is zero)");
        }


        if (currentPrice.compareTo(upperBand) > 0) {
            result.setSignal(Signal.SELL); // Preço acima da banda superior, potencial reversão ou forte sobrecompra
            signalDetails += " Preço acima da Banda Superior (Potencial Venda/Sobrecompra).";
        } else if (currentPrice.compareTo(lowerBand) < 0) {
            result.setSignal(Signal.BUY); // Preço abaixo da banda inferior, potencial reversão ou forte sobrevenda
            signalDetails += " Preço abaixo da Banda Inferior (Potencial Compra/Sobrevenda).";
        } else {
            // Poderíamos verificar "Squeeze" (bandas estreitas)
            // Largura da Banda = (Banda Superior - Banda Inferior) / Banda Média
            BigDecimal bandRange = upperBand.subtract(lowerBand);
            if (middleBand.compareTo(BigDecimal.ZERO) != 0) { // Evitar divisão por zero
                BigDecimal bandWidthPercentage = bandRange.divide(middleBand, CALC_SCALE, RoundingMode.HALF_UP)
                                                       .multiply(BigDecimal.valueOf(100));
                result.addValue("bandWidthPercentage", bandWidthPercentage.setScale(SCALE, RoundingMode.HALF_UP));
                if (bandWidthPercentage.compareTo(new BigDecimal("5")) < 0) { // Exemplo: < 5% é um squeeze
                    signalDetails += " Squeeze detectado (Bandas estreitas).";
                }
            }
            result.setSignal(Signal.NEUTRAL);
             signalDetails += " Preço dentro das bandas.";
        }
        result.setDetails(signalDetails);
        return result;
    }
}