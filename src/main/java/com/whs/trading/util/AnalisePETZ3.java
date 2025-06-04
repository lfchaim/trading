package com.whs.trading.util;

import java.util.List;

public class AnalisePETZ3 {

	public static void main(String[] args) {
		// Dados históricos fictícios de PETZ3 (preços de fechamento)
		List<Double> precos = List.of(6.20, 6.15, 6.30, 6.10, 5.90, 6.00, 5.80, 5.70, 5.95, 6.10, 6.25, 6.40, 6.50,
				6.45, 6.60, 6.75, 7.00, 7.20, 7.10, 6.90);

		// 1. Média Móvel Simples (MMS) de 5 dias
		MediaMovel.calcularMediaMovel(precos, 5);

		// 2. Índice de Força Relativa (RSI) - 14 períodos
		RSI.calcularRSI(precos, 14);

		// 3. Identificar suportes e resistências
		SuporteResistencia.identificarSuportesResistencias(precos);

		// 4. Recomendação com base nos indicadores
		RecomendacaoPETZ3.gerarRecomendacao(precos);
	}

}