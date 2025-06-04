package com.whs.trading.util;

import java.util.ArrayList;
import java.util.List;

public class RecomendacaoPETZ3 {

	public static void gerarRecomendacao(List<Double> precos) {
		if (precos == null || precos.size() < 20) {
			System.out.println("Dados insuficientes para gerar recomendação.");
			return;
		}

		// 1. Coletar indicadores técnicos
		double precoAtual = precos.get(precos.size() - 1);
		double mediaMovel5 = calcularMediaMovel(precos, 5);
		double mediaMovel10 = calcularMediaMovel(precos, 10);
		double mediaMovel20 = calcularMediaMovel(precos, 20);
		double rsi = calcularRSI(precos, 14);
		double[] bollinger = calcularBollingerBands(precos, 20, 2);
		double bandaSuperior = bollinger[0];
		double bandaInferior = bollinger[1];

		// 2. Exibir dados
		System.out.println("\n=== INDICADORES TÉCNICOS ===");
		System.out.printf("Preço Atual: R$ %.2f\n", precoAtual);
		System.out.printf("Médias Móveis: MMS-5 (%.2f) | MMS-10 (%.2f) | MMS-20 (%.2f)\n", mediaMovel5, mediaMovel10,
				mediaMovel20);
		System.out.printf("RSI (14 dias): %.2f\n", rsi);
		System.out.printf("Bandas de Bollinger: Superior (%.2f) | Inferior (%.2f)\n", bandaSuperior, bandaInferior);

		// 3. Lógica de Recomendação
		System.out.println("\n=== RECOMENDAÇÃO ===");

		// Tendência Principal (Médias Móveis)
		if (mediaMovel5 > mediaMovel10 && mediaMovel10 > mediaMovel20) {
			System.out.println("Tendência: FORTE ALTA 📈");
		} else if (mediaMovel5 < mediaMovel10 && mediaMovel10 < mediaMovel20) {
			System.out.println("Tendência: FORTE BAIXA 📉");
		} else {
			System.out.println("Tendência: NEUTRA OU LATERAL ↔️");
		}

		// RSI (Sobrecompra/Sobrevenda)
		if (rsi > 70) {
			System.out.println("RSI: SOBRECOMPRADO (Atenção para possível correção) ⚠️");
		} else if (rsi < 30) {
			System.out.println("RSI: SOBREVENDIDO (Oportunidade potencial) ✅");
		} else {
			System.out.println("RSI: NEUTRO");
		}

		// Bandas de Bollinger
		if (precoAtual > bandaSuperior) {
			System.out.println("Preço PRÓXIMO da Banda Superior (Possível resistência) ⬆️");
		} else if (precoAtual < bandaInferior) {
			System.out.println("Preço PRÓXIMO da Banda Inferior (Possível suporte) ⬇️");
		}

		// Recomendação Final
		System.out.println("\n🔎 Estratégia Sugerida:");
		if (mediaMovel5 > mediaMovel10 && rsi < 70 && precoAtual < bandaSuperior) {
			System.out.println("- COMPRA (Alvo: Resistência próxima)");
			System.out.printf("- Stop Loss: Abaixo de R$ %.2f (Suporte mais recente)\n",
					encontrarSuporteRecente(precos));
		} else if (mediaMovel5 < mediaMovel10 && rsi > 30 && precoAtual > bandaInferior) {
			System.out.println("- VENDA PARCIAL (Proteção de ganhos)");
			System.out.printf("- Take Profit: R$ %.2f (Resistência próxima)\n", encontrarResistenciaRecente(precos));
		} else {
			System.out.println("- AGUARDAR (Mercado sem tendência definida)");
		}
	}

	// ========== MÉTODOS AUXILIARES ==========

	private static double calcularMediaMovel(List<Double> precos, int periodo) {
		if (precos.size() < periodo)
			return 0;
		return precos.subList(precos.size() - periodo, precos.size()).stream().mapToDouble(Double::doubleValue)
				.average().orElse(0);
	}

	private static double calcularRSI(List<Double> precos, int periodo) {
		if (precos.size() <= periodo)
			return 0;
		List<Double> ganhos = new ArrayList<>();
		List<Double> perdas = new ArrayList<>();

		for (int i = precos.size() - periodo; i < precos.size() - 1; i++) {
			double variacao = precos.get(i + 1) - precos.get(i);
			ganhos.add(variacao > 0 ? variacao : 0);
			perdas.add(variacao < 0 ? Math.abs(variacao) : 0);
		}

		double mediaGanhos = ganhos.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		double mediaPerdas = perdas.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		return mediaPerdas == 0 ? 100 : 100 - (100 / (1 + (mediaGanhos / mediaPerdas)));
	}

	private static double[] calcularBollingerBands(List<Double> precos, int periodo, int desvioPadrao) {
		if (precos.size() < periodo)
			return new double[] { 0, 0 };
		List<Double> sublista = precos.subList(precos.size() - periodo, precos.size());
		double media = sublista.stream().mapToDouble(Double::doubleValue).average().orElse(0);
		double variancia = sublista.stream().mapToDouble(v -> Math.pow(v - media, 2)).average().orElse(0);
		double desvio = Math.sqrt(variancia);
		return new double[] { media + (desvioPadrao * desvio), media - (desvioPadrao * desvio) };
	}

	private static double encontrarSuporteRecente(List<Double> precos) {
		return precos.stream().mapToDouble(Double::doubleValue).min().orElse(0);
	}

	private static double encontrarResistenciaRecente(List<Double> precos) {
		return precos.stream().mapToDouble(Double::doubleValue).max().orElse(0);
	}
}