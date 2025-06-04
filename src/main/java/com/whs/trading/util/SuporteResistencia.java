package com.whs.trading.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SuporteResistencia {

	public static void identificarSuportesResistencias(List<Double> precos) {
		if (precos == null || precos.size() < 3) {
			System.out.println("Dados insuficientes para análise.");
			return;
		}

		List<Double> suportes = new ArrayList<>();
		List<Double> resistencias = new ArrayList<>();

		// Identifica mínimos locais (suportes) e máximos locais (resistências)
		for (int i = 1; i < precos.size() - 1; i++) {
			double anterior = precos.get(i - 1);
			double atual = precos.get(i);
			double proximo = precos.get(i + 1);

			// Verifica se é um mínimo local (suporte)
			if (atual < anterior && atual < proximo) {
				suportes.add(atual);
			}
			// Verifica se é um máximo local (resistência)
			else if (atual > anterior && atual > proximo) {
				resistencias.add(atual);
			}
		}

		// Filtra níveis significativos (evita ruído)
		List<Double> suportesSignificativos = filtrarNiveisProximos(suportes, 0.03); // 3% de tolerância
		List<Double> resistenciasSignificativas = filtrarNiveisProximos(resistencias, 0.03);

		// Exibe resultados
		System.out.println("\n=== NÍVEIS DE SUPORTE ===");
		if (suportesSignificativos.isEmpty()) {
			System.out.println("Nenhum suporte significativo identificado.");
		} else {
			System.out.println(suportesSignificativos.stream().map(p -> String.format("R$ %.2f", p))
					.collect(Collectors.joining(", ")));
		}

		System.out.println("\n=== NÍVEIS DE RESISTÊNCIA ===");
		if (resistenciasSignificativas.isEmpty()) {
			System.out.println("Nenhuma resistência significativa identificada.");
		} else {
			System.out.println(resistenciasSignificativas.stream().map(p -> String.format("R$ %.2f", p))
					.collect(Collectors.joining(", ")));
		}
	}

	// Método auxiliar para agrupar níveis próximos (evita duplicatas)
	private static List<Double> filtrarNiveisProximos(List<Double> niveis, double tolerancia) {
		List<Double> filtrados = new ArrayList<>();
		for (double nivel : niveis) {
			boolean isNovo = true;
			for (double existente : filtrados) {
				if (Math.abs(nivel - existente) / existente <= tolerancia) {
					isNovo = false;
					break;
				}
			}
			if (isNovo) {
				filtrados.add(nivel);
			}
		}
		return filtrados;
	}

	// Exemplo de uso
	public static void main(String[] args) {
		List<Double> precosPETZ3 = List.of(6.20, 6.15, 6.30, 6.10, 5.90, 6.00, 5.80, 5.70, 5.95, 6.10, 6.25, 6.40, 6.50, 6.45, 6.60, 6.75, 7.00, 7.20, 7.10, 6.90);
		identificarSuportesResistencias(precosPETZ3);
	}
}