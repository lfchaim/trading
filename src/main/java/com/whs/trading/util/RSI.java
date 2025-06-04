package com.whs.trading.util;

import java.util.ArrayList;
import java.util.List;

public class RSI {

	// Cálculo do RSI
	public static void calcularRSI(List<Double> precos, int periodo) {
		System.out.println("\n=== RSI (" + periodo + " dias) ===");
		List<Double> ganhos = new ArrayList<>();
		List<Double> perdas = new ArrayList<>();

		ganhos.add(0.0);
		perdas.add(0.0);
		for (int i = 1; i < precos.size(); i++) {
			double diferenca = precos.get(i) - precos.get(i - 1);
			if (diferenca > 0) {
				ganhos.add(diferenca);
				perdas.add(0.0);
			} else {
				ganhos.add(0.0);
				perdas.add(Math.abs(diferenca));
			}
		}

		double mediaGanhos = ganhos.subList(0, periodo).stream().mapToDouble(Double::doubleValue).average().orElse(0);
		double mediaPerdas = perdas.subList(0, periodo).stream().mapToDouble(Double::doubleValue).average().orElse(0);
		System.out.println("Preço size: "+precos.size()+" Ganhos: "+ganhos.size()+" Perdas: "+perdas.size());
		for (int i = periodo; i < precos.size(); i++) {
			double rsi = 100 - (100 / (1 + (mediaGanhos / mediaPerdas)));
			System.out.printf("Dia %d: R$ %.2f (RSI: %.2f)%n", i + 1, precos.get(i), rsi);

			// Atualiza médias para o próximo dia (método simplificado)
			mediaGanhos = ((mediaGanhos * (periodo - 1)) + ganhos.get(i)) / periodo;
			mediaPerdas = ((mediaPerdas * (periodo - 1)) + perdas.get(i)) / periodo;
		}
	}
	
}
