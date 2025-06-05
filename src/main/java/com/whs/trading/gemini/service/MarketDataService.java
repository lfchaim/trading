package com.whs.trading.gemini.service;

import java.util.List;

import com.whs.trading.gemini.dto.CandlestickData;

public interface MarketDataService {
    /**
     * Busca dados históricos de candlestick para um símbolo e intervalo específicos.
     *
     * @param symbol O símbolo do par (ex: "BTCUSDT").
     * @param interval O intervalo da vela (ex: "1h", "4h", "1d").
     * @param limit O número de velas a serem retornadas (padrão Binance 500, máx 1000).
     * @param startTime Timestamp de início em milissegundos (opcional).
     * @param endTime Timestamp de fim em milissegundos (opcional).
     * @return Uma lista de CandlestickData.
     */
    List<CandlestickData> getHistoricalCandlesticks(String symbol, String interval, Integer limit, Long startTime, Long endTime);
}