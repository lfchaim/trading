package com.whs.trading.util;

import java.util.List;

public class MediaMovel {
	
	// Cálculo da Média Móvel Simples
	public static void calcularMediaMovel(List<Double> precos, int periodo) {
		System.out.println("\n=== Média Móvel (" + periodo + " dias) ===");
		for (int i = periodo - 1; i < precos.size(); i++) {
			double soma = 0;
			for (int j = i; j >= i - periodo + 1; j--) {
				soma += precos.get(j);
			}
			double media = soma / periodo;
			System.out.printf("Dia %d: R$ %.2f (MMS: %.2f)%n", i + 1, precos.get(i), media);
		}
	}

}
