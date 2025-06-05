package com.whs.trading.gemini.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.whs.trading.gemini.dto.CandlestickData;

@Service
public class BinanceMarketDataService implements MarketDataService {

    private static final Logger logger = LoggerFactory.getLogger(BinanceMarketDataService.class);
    private static final String BINANCE_API_BASE_URL = "https://api.binance.com/api/v3/klines";

    private final RestTemplate restTemplate;

    @Autowired
    public BinanceMarketDataService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<CandlestickData> getHistoricalCandlesticks(String symbol, String interval, Integer limit, Long startTime, Long endTime) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(BINANCE_API_BASE_URL)
                .queryParam("symbol", symbol.toUpperCase())
                .queryParam("interval", interval);

        if (limit != null) {
            builder.queryParam("limit", limit);
        }
        if (startTime != null) {
            builder.queryParam("startTime", startTime);
        }
        if (endTime != null) {
            builder.queryParam("endTime", endTime);
        }

        String url = builder.toUriString();
        logger.info("Fetching klines from Binance API: {}", url);

        try {
            // A API da Binance retorna um array de arrays: Object[][]
            ResponseEntity<Object[][]> response = restTemplate.getForEntity(url, Object[][].class);
            Object[][] klines = response.getBody();

            if (klines == null) {
                logger.warn("Received null body from Binance API for URL: {}", url);
                return Collections.emptyList();
            }

            List<CandlestickData> candlestickDataList = new ArrayList<>();
            for (Object[] kline : klines) {
                // Estrutura de um kline da Binance:
                // 0: Open time (Long)
                // 1: Open price (String)
                // 2: High price (String)
                // 3: Low price (String)
                // 4: Close price (String)
                // 5: Volume (String)
                // 6: Close time (Long)
                // 7: Quote asset volume (String)
                // 8: Number of trades (Integer)
                // 9: Taker buy base asset volume (String)
                // 10: Taker buy quote asset volume (String)
                // 11: Ignore (String)

                // Validar que temos pelo menos os 7 campos esperados
                if (kline.length < 7) {
                    logger.warn("Kline data has insufficient fields: {}", (Object) kline); // Cast para evitar varargs warning
                    continue; 
                }

                try {
                    Instant openTime = Instant.ofEpochMilli((Long) kline[0]);
                    BigDecimal openPrice = new BigDecimal((String) kline[1]);
                    BigDecimal highPrice = new BigDecimal((String) kline[2]);
                    BigDecimal lowPrice = new BigDecimal((String) kline[3]);
                    BigDecimal closePrice = new BigDecimal((String) kline[4]);
                    BigDecimal volume = new BigDecimal((String) kline[5]);
                    Instant closeTime = Instant.ofEpochMilli((Long) kline[6]);

                    candlestickDataList.add(new CandlestickData(
                            openTime, openPrice, highPrice, lowPrice, closePrice, volume, closeTime
                    ));
                } catch (ClassCastException | NumberFormatException e) {
                    logger.error("Error parsing kline data field: {} for kline: {}", e.getMessage(), (Object) kline, e);
                }
            }
            logger.info("Successfully fetched and parsed {} candlesticks for {} interval {}", candlestickDataList.size(), symbol, interval);
            return candlestickDataList;

        } catch (HttpClientErrorException e) {
            logger.error("HTTP Client Error when calling Binance API: {} - {}", e.getStatusCode(), e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            logger.error("An unexpected error occurred when calling Binance API: {}", e.getMessage(), e);
        }
        return Collections.emptyList(); // Retorna lista vazia em caso de erro
    }
}