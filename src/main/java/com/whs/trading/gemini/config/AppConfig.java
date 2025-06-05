package com.whs.trading.gemini.config;

import java.time.Duration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // Você pode configurar timeouts e outros aspectos aqui
        return builder
                .setConnectTimeout(Duration.ofSeconds(5)) // Timeout de conexão
                .setReadTimeout(Duration.ofSeconds(10))    // Timeout de leitura
                .build();
    }
}