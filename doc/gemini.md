# Prompt

Criar um projeto Spring Boot que forneça o valor de compra e venda de uma criptomoeda, como BTC, no momento da consulta. A aplicação deve incorporar uma variedade de algoritmos reconhecidos para maximizar a precisão dos resultados, tais como:
1. RSI (Índice de Força Relativa)
2. Média Móvel
3. Suportes Resistenciais

**Requisitos:**
1. **Arquitetura e Desenvolvimento:**
   - Implementar em Spring Boot.
   - Utilizar design modular para facilitar a inclusão de novos algoritmos.

2. **Algoritmos de Análise:**
   - Integrar algoritmos adicionais conforme identificados na pesquisa, além dos mencionados.

3. **Dados de Mercado:**
   - Acessar APIs de mercado para dados em tempo real.

4. **Validação e Testes:**
   - Testar a precisão dos algoritmos com dados históricos.
   - Implementar testes automatizados para garantir a confiabilidade.

5. **Segurança e Desempenho:**
   - Garantir segurança nas consultas e na manipulação de dados.
   - Otimizar o desempenho para respostas em tempo real.

6. **Documentação:**
   - Fornecer documentação completa para configuração e inclusão de novos algoritmos.

7. **Expansão:**
   - Estruturar o projeto para fácil expansão e integração de novas funcionalidades.

**Instruções Adicionais:**
- Estabelecer mecanismos de logs para monitoramento e análise de desempenho.
- Considerar a usabilidade e interface amigável para demonstração clara dos resultados.

O projeto deve ser construído com foco em escalabilidade e manutenção, considerando as melhores práticas do setor.

# Resposta

Ótimo projeto! Construir um sistema de análise de criptomoedas com Spring Boot, focado em precisão e modularidade, é um desafio interessante e com grande potencial de aprendizado.

Vamos detalhar um plano de projeto para atender aos seus requisitos.

---

## Plano de Projeto: Análise de Criptomoedas com Spring Boot

Este projeto visa criar uma aplicação Spring Boot que fornece recomendações de compra/venda para criptomoedas (inicialmente BTC) baseadas em múltiplos algoritmos de análise técnica.

---

### 1. Arquitetura e Desenvolvimento

A arquitetura será baseada em microserviços ou módulos bem definidos dentro de uma aplicação Spring Boot.

* **Framework:** Spring Boot
* **Linguagem:** Java (ou Kotlin)
* **Gerenciador de Dependências:** Maven ou Gradle
* **Design Modular:**
    * `core-module`: Contém entidades de domínio (ex: `Candlestick`, `AnalysisResult`, `Signal`), interfaces para os algoritmos e serviços centrais.
    * `data-access-module`: Responsável por interagir com APIs externas para buscar dados de mercado.
    * `analysis-engine-module`: Contém as implementações dos algoritmos de análise. Cada algoritmo pode ser um `Service` ou `Component` Spring.
    * `api-module`: Expõe os endpoints REST para consulta dos resultados da análise.
    * `scheduler-module` (Opcional): Para tarefas agendadas, como pré-processamento ou cache de dados.

**Estrutura de Pacotes Sugerida:**

```
com.example.cryptanalyzer
├── CryptanalyzerApplication.java
├── config        // Configurações gerais, Beans, Segurança
├── controller    // Endpoints REST (ex: CryptoAnalysisController)
├── dto           // Data Transfer Objects (ex: AnalysisRequest, AnalysisResponse, IndicatorResult)
├── model         // Entidades de domínio (ex: CandlestickData, Signal)
├── service
│   ├── MarketDataService.java      // Interface para buscar dados de mercado
│   ├── AnalysisOrchestratorService.java // Orquestra os diferentes algoritmos
│   └── indicator                 // Pacote para as interfaces e implementações dos indicadores
│       ├── TechnicalIndicator.java // Interface comum para todos os indicadores
│       ├── RsiService.java
│       ├── MovingAverageService.java
│       ├── SupportResistanceService.java
│       └── ... // Outros algoritmos
├── client        // Clientes para APIs externas (ex: BinanceClient, CoinGeckoClient)
├── repository    // (Opcional) Se precisar persistir dados históricos para backtesting
├── exception     // Handlers de exceções globais
└── util          // Classes utilitárias
```

**Interface para Algoritmos:**

Para facilitar a inclusão de novos algoritmos, definiremos uma interface comum:

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import java.util.List;

public interface TechnicalIndicator {
    String getName(); // Retorna o nome do indicador
    IndicatorResult analyze(List<CandlestickData> historicalData, String symbol);
    // Poderia receber parâmetros específicos do indicador, se necessário
}
```

O `AnalysisOrchestratorService` injetaria uma lista de `TechnicalIndicator` e executaria cada um.

---

### 2. Algoritmos de Análise

Além dos solicitados, podemos incluir outros populares:

* **RSI (Índice de Força Relativa):** Mede a velocidade e a mudança dos movimentos de preços.
    * Sinal: Compra abaixo de 30 (sobrevenda), Venda acima de 70 (sobrecompra).
* **Médias Móveis (MA):** Suavizam os dados de preço para formar um indicador de tendência.
    * Simples (SMA), Exponencial (EMA).
    * Sinal: Cruzamento de MAs (curta sobre longa = compra, longa sobre curta = venda), preço cruzando MA.
* **Suportes e Resistências (S/R):** Níveis de preço onde a tendência tende a pausar ou reverter.
    * Identificação: Pontos de pivô, topos/fundos anteriores, números redondos.
    * Sinal: Compra perto do suporte, Venda perto da resistência.
* **MACD (Convergência/Divergência de Médias Móveis):** Mostra a relação entre duas MAs exponenciais.
    * Sinal: Cruzamento da linha MACD com a linha de sinal, cruzamento da linha zero, divergências.
* **Bandas de Bollinger:** Medem a volatilidade do mercado.
    * Sinal: Preço tocando banda inferior (potencial compra), preço tocando banda superior (potencial venda), "squeeze".
* **Estocástico (Stochastic Oscillator):** Compara o preço de fechamento de um ativo com seu range de preços durante um período.
    * Sinal: Similar ao RSI, com níveis de sobrecompra/sobrevenda.

**Implementação:**
Cada algoritmo será uma classe que implementa `TechnicalIndicator`.
O `AnalysisOrchestratorService` coletará os resultados e poderá, opcionalmente, aplicar uma lógica de ponderação ou consenso para gerar uma recomendação final (COMPRA, VENDA, MANTER).

---

### 3. Dados de Mercado

Será necessário integrar com APIs de exchanges ou provedores de dados.

* **Provedores Populares:**
    * **Binance API:** Oferece dados de mercado em tempo real (via WebSockets ou REST), dados históricos (OHLCV - Open, High, Low, Close, Volume). Gratuita com limites de requisição.
    * **CoinGecko API:** Ampla cobertura de moedas, dados de mercado, históricos. Plano gratuito generoso.
    * **Alpha Vantage API:** Dados de cripto e mercados tradicionais. Plano gratuito com limitações.
    * **Coinbase Pro API:** Para dados da exchange Coinbase.

* **Implementação:**
    * Usar `RestTemplate` ou `WebClient` (reativo) do Spring para fazer as chamadas HTTP.
    * Criar um `MarketDataService` que abstrai a fonte dos dados.
    * Mapear as respostas JSON para DTOs/Modelos Java (ex: `CandlestickData`).
    * Gerenciar chaves de API de forma segura (ex: via variáveis de ambiente ou Spring Cloud Config).

**Exemplo de `CandlestickData`:**

```java
package com.example.cryptanalyzer.model;

import java.math.BigDecimal;
import java.time.Instant;

public class CandlestickData {
    private Instant openTime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private Instant closeTime;
    // Getters e Setters
}
```

---

### 4. Validação e Testes

* **Testes Unitários (JUnit, Mockito):**
    * Testar a lógica de cada algoritmo individualmente com datasets de entrada conhecidos e resultados esperados.
    * Mockar o `MarketDataService` para não depender de chamadas reais à API durante os testes dos serviços de análise.
    * Testar a lógica do `AnalysisOrchestratorService`.
    * Testar os `Controllers`.
* **Testes de Integração (`@SpringBootTest`):**
    * Testar o fluxo completo desde a chamada da API até a resposta, podendo usar um servidor de API mockado (ex: WireMock) ou, com cuidado, APIs reais em ambiente de teste.
* **Backtesting com Dados Históricos:**
    * Obter uma grande quantidade de dados históricos da API escolhida.
    * Simular a execução dos algoritmos sobre esses dados e comparar os sinais gerados com os movimentos de preço subsequentes para avaliar a "precisão" ou rentabilidade teórica das estratégias. Isso é mais complexo e pode ser um módulo à parte.
    * Inicialmente, a "precisão" pode ser validada pela correta implementação matemática dos indicadores.

---

### 5. Segurança e Desempenho

* **Segurança:**
    * **Chaves de API:** Não commitar chaves no código. Usar `application.properties` (ignoradas pelo git), variáveis de ambiente ou, em produção, Spring Cloud Config Server / HashiCorp Vault.
    * **Comunicação:** Usar HTTPS para todas as chamadas externas e, se a aplicação for exposta publicamente, para seus próprios endpoints.
    * **Validação de Entrada:** Validar parâmetros de requisição (ex: símbolo da criptomoeda).
    * **Rate Limiting:** Implementar rate limiting nos seus endpoints para prevenir abuso, se necessário (Spring Cloud Gateway ou filtros customizados).
    * **Dependências:** Manter as dependências do Spring Boot e outras bibliotecas atualizadas.
* **Desempenho:**
    * **Caching:** Implementar cache para dados de mercado que não mudam com tanta frequência ou para resultados de análises se os parâmetros forem os mesmos (Spring Cache com Caffeine ou Redis).
    * **Chamadas Assíncronas:** Se a análise envolver múltiplas chamadas de API demoradas ou cálculos intensivos, usar `@Async` do Spring para operações não bloqueantes. `WebClient` é naturalmente não bloqueante.
    * **Otimização de Algoritmos:** Garantir que os cálculos dos indicadores sejam eficientes, especialmente com grandes datasets históricos.
    * **Consultas em Tempo Real:** A "real-time" aqui geralmente significa "near real-time". A latência dependerá da API externa e da complexidade da análise.

---

### 6. Documentação

* **README.md:**
    * Descrição do projeto.
    * Como configurar o ambiente de desenvolvimento.
    * Como executar a aplicação.
    * Instruções para configurar chaves de API.
    * Exemplos de uso da API (endpoints, payloads).
* **Documentação da API:**
    * Usar Springdoc OpenAPI (Swagger) para gerar documentação interativa da API REST. Adicionar a dependência `springdoc-openapi-starter-webmvc-ui`.
* **Documentação de Código:**
    * Comentários Javadoc para classes e métodos públicos, especialmente para a interface `TechnicalIndicator` e suas implementações, explicando a lógica e os parâmetros.
* **Guia para Adicionar Novos Algoritmos:**
    * Um documento (pode ser no README ou em um arquivo `CONTRIBUTING.md`) explicando o processo:
        1.  Criar uma nova classe que implementa `TechnicalIndicator`.
        2.  Implementar os métodos `getName()` e `analyze()`.
        3.  Anotar a classe com `@Service` ou `@Component` para que o Spring a detecte.
        4.  O `AnalysisOrchestratorService` já deverá estar configurado para injetar e usar todas as implementações de `TechnicalIndicator`.

---

### 7. Expansão

* **Modularidade:** A arquitetura já visa facilitar a expansão.
* **Novas Criptomoedas:** O símbolo da criptomoeda deve ser um parâmetro nas chamadas da API, tornando trivial a análise de diferentes moedas, desde que suportadas pela API de dados.
* **Novos Algoritmos:** Conforme descrito na seção de documentação, adicionar novos algoritmos é simples.
* **Novas Fontes de Dados:** Criar novas implementações de `MarketDataService` para diferentes APIs. Usar um `Factory` ou perfis Spring para selecionar a fonte de dados.
* **Estratégias de Agregação de Sinais:** Melhorar como os sinais de diferentes indicadores são combinados (ex: sistema de pontuação, machine learning básico).
* **Interface de Usuário (Opcional):** Embora o foco seja uma API, uma interface simples com Thymeleaf ou um frontend separado (React, Vue, Angular) poderia ser adicionada para visualização.
* **Notificações:** Integrar com serviços de notificação (email, Telegram) para alertas baseados nos sinais.
* **Persistência:** Adicionar um banco de dados (ex: PostgreSQL, MongoDB) para armazenar dados históricos coletados, resultados de análises para auditoria ou backtesting mais avançado.

---

### Instruções Adicionais

* **Mecanismos de Logs (Monitoramento e Análise):**
    * Utilizar SLF4J com Logback (padrão no Spring Boot).
    * Logar eventos importantes:
        * Início e fim de chamadas a APIs externas.
        * Resultados de cada indicador.
        * Decisão final da análise.
        * Erros e exceções.
        * Tempo de processamento das análises.
    * Configurar níveis de log apropriados (DEBUG, INFO, WARN, ERROR).
    * Estruturar logs para fácil parseamento por ferramentas de monitoramento (ex: ELK Stack, Grafana Loki).
* **Usabilidade e Interface Amigável (para demonstração):**
    * **API REST:**
        * Endpoints claros e intuitivos (ex: `GET /api/v1/analysis/{symbol}?interval=1h&limit=200`).
        * Respostas JSON bem estruturadas, contendo:
            * Dados da consulta (símbolo, parâmetros).
            * Resultado de cada indicador (valor, sinal).
            * Um sinal consolidado (COMPRA, VENDA, MANTER).
            * Preço atual no momento da consulta.
    * **Demonstração:**
        * Se uma UI não for desenvolvida, usar ferramentas como Postman ou `curl` para demonstrar as chamadas à API e os resultados.
        * A documentação Swagger UI fornecerá uma interface interativa para testar os endpoints.

---

### Exemplo de Endpoint e Resposta

**Requisição:**
`GET /api/v1/analysis/BTCUSDT?rsiPeriod=14&shortMa=10&longMa=50`

**Resposta JSON (Exemplo):**

```json
{
  "symbol": "BTCUSDT",
  "timestamp": "2025-06-04T22:00:00Z",
  "currentPrice": 68500.50,
  "parameters": {
    "rsiPeriod": 14,
    "shortMaPeriod": 10,
    "longMaPeriod": 50
    // ... outros parâmetros usados
  },
  "indicatorResults": [
    {
      "name": "RSI",
      "value": 35.5,
      "signal": "NEUTRAL", // Ou "POTENTIAL_BUY"
      "details": "RSI period: 14"
    },
    {
      "name": "Moving Average Crossover",
      "signal": "BUY",
      "details": "SMA(10) crossed above SMA(50)"
    },
    {
      "name": "Support/Resistance",
      "signal": "NEUTRAL",
      "details": {
        "support": 67000.00,
        "resistance": 70000.00,
        "message": "Price is between support and resistance."
      }
    }
    // ... outros indicadores
  ],
  "consolidatedSignal": "BUY", // Lógica de consolidação (ex: maioria, ponderado)
  "recommendation": {
    "action": "BUY", // BUY, SELL, HOLD
    "confidence": "MEDIUM", // HIGH, MEDIUM, LOW
    "suggestedBuyPrice": 68500.50, // Preço atual ou ligeiramente abaixo
    "targetPrice": 70000.00, // Exemplo, pode vir da Resistência
    "stopLossPrice": 67500.00 // Exemplo, pode vir do Suporte
  }
}
```

---

### Próximos Passos (Implementação Inicial)

1.  **Configurar o Projeto Spring Boot:** Com as dependências básicas (Web, Lombok, Springdoc OpenAPI).
2.  **Definir Modelos e DTOs:** `CandlestickData`, `IndicatorResult`, `AnalysisResponse`.
3.  **Implementar `MarketDataService`:** Começar com uma API (ex: CoinGecko ou Binance) para buscar dados OHLCV.
4.  **Implementar a Interface `TechnicalIndicator` e um Algoritmo Simples:** Começar com RSI ou uma Média Móvel Simples.
5.  **Implementar `AnalysisOrchestratorService`:** Para chamar o(s) indicador(es).
6.  **Criar o `CryptoAnalysisController`:** Para expor o endpoint.
7.  **Adicionar Testes Unitários:** Para o algoritmo e o serviço.
8.  **Configurar Logs Básicos.**
9.  Iterativamente adicionar os outros algoritmos e funcionalidades.

Este plano detalhado deve fornecer uma base sólida para iniciar o desenvolvimento do seu projeto. Lembre-se de começar pequeno, iterar e testar continuamente. Boa sorte!

Okay, vamos focar nas classes dos algoritmos. Abaixo, detalharei a estrutura e exemplos de implementação para os algoritmos RSI, Média Móvel (SMA) e Suporte/Resistência em um projeto Spring Boot.

Primeiro, vamos definir algumas estruturas de dados comuns e a interface que todos os indicadores técnicos implementarão.

### Estruturas de Dados e Interface Comum

**1. `CandlestickData.java` (Dados de Vela)**
Representa uma única vela OHLCV (Open, High, Low, Close, Volume).

```java
package com.example.cryptanalyzer.model;

import java.math.BigDecimal;
import java.time.Instant;

public class CandlestickData {
    private Instant openTime;
    private BigDecimal open;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal close;
    private BigDecimal volume;
    private Instant closeTime;

    // Construtores, Getters e Setters
    public CandlestickData(Instant openTime, BigDecimal open, BigDecimal high, BigDecimal low, BigDecimal close, BigDecimal volume, Instant closeTime) {
        this.openTime = openTime;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        this.closeTime = closeTime;
    }

    public Instant getOpenTime() { return openTime; }
    public void setOpenTime(Instant openTime) { this.openTime = openTime; }
    public BigDecimal getOpen() { return open; }
    public void setOpen(BigDecimal open) { this.open = open; }
    public BigDecimal getHigh() { return high; }
    public void setHigh(BigDecimal high) { this.high = high; }
    public BigDecimal getLow() { return low; }
    public void setLow(BigDecimal low) { this.low = low; }
    public BigDecimal getClose() { return close; }
    public void setClose(BigDecimal close) { this.close = close; }
    public BigDecimal getVolume() { return volume; }
    public void setVolume(BigDecimal volume) { this.volume = volume; }
    public Instant getCloseTime() { return closeTime; }
    public void setCloseTime(Instant closeTime) { this.closeTime = closeTime; }
}
```

**2. `Signal.java` (Enum de Sinal)**
Define os possíveis sinais de negociação.

```java
package com.example.cryptanalyzer.model;

public enum Signal {
    BUY,
    SELL,
    HOLD, // Sugere manter a posição atual
    NEUTRAL // Sem sinal claro
}
```

**3. `IndicatorResult.java` (Resultado do Indicador)**
Encapsula o resultado da análise de um indicador.

```java
package com.example.cryptanalyzer.dto;

import com.example.cryptanalyzer.model.Signal;
import java.util.Map;
import java.util.HashMap;

public class IndicatorResult {
    private String indicatorName;
    private Signal signal;
    private Map<String, Object> values; // Para armazenar valores calculados (ex: valor do RSI, níveis de S/R)
    private String details; // Descrição textual do resultado

    public IndicatorResult(String indicatorName) {
        this.indicatorName = indicatorName;
        this.values = new HashMap<>();
    }

    // Getters e Setters
    public String getIndicatorName() { return indicatorName; }
    public void setIndicatorName(String indicatorName) { this.indicatorName = indicatorName; }
    public Signal getSignal() { return signal; }
    public void setSignal(Signal signal) { this.signal = signal; }
    public Map<String, Object> getValues() { return values; }
    public void setValues(Map<String, Object> values) { this.values = values; }
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public void addValue(String key, Object value) {
        this.values.put(key, value);
    }
}
```

**4. `TechnicalIndicator.java` (Interface do Indicador Técnico)**
Interface que todos os algoritmos de análise implementarão.

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import java.util.List;
import java.util.Map;

public interface TechnicalIndicator {
    String getName(); // Nome do indicador (ex: "RSI", "SMA")
    IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params);
    // 'params' pode conter configurações como período do RSI, períodos das MAs, etc.
    // A lista historicalData deve estar ordenada do mais antigo para o mais recente.
}
```

---

### Implementação das Classes dos Algoritmos

**1. RSI (Índice de Força Relativa)**

O RSI mede a magnitude das recentes variações de preço para avaliar condições de sobrecompra ou sobrevenda.

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import com.example.cryptanalyzer.model.Signal;
import org.springframework.stereotype.Service; // Importa a anotação Service

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service // Para que o Spring gerencie este bean
public class RsiIndicator implements TechnicalIndicator {

    private static final String NAME = "RSI";
    private static final int DEFAULT_PERIOD = 14;
    private static final int SCALE = 4; // Escala para precisão do BigDecimal

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int period = params.containsKey("rsiPeriod") ? Integer.parseInt(params.get("rsiPeriod")) : DEFAULT_PERIOD;

        if (historicalData == null || historicalData.size() < period + 1) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para calcular o RSI com período " + period);
            return result;
        }

        List<BigDecimal> gains = new ArrayList<>();
        List<BigDecimal> losses = new ArrayList<>();

        // Calcula as variações de preço (ganhos e perdas)
        for (int i = 1; i < historicalData.size(); i++) {
            BigDecimal difference = historicalData.get(i).getClose().subtract(historicalData.get(i - 1).getClose());
            if (difference.compareTo(BigDecimal.ZERO) > 0) {
                gains.add(difference);
                losses.add(BigDecimal.ZERO);
            } else {
                gains.add(BigDecimal.ZERO);
                losses.add(difference.abs());
            }
        }
        
        if (gains.size() < period) { // Precisamos de 'period' ganhos/perdas para a primeira média
             result.setSignal(Signal.NEUTRAL);
             result.setDetails("Dados de ganhos/perdas insuficientes após cálculo de diferenças para o período " + period);
             return result;
        }

        // Calcula a média dos ganhos e perdas para o primeiro período
        BigDecimal avgGain = gains.subList(0, period).stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        BigDecimal avgLoss = losses.subList(0, period).stream().reduce(BigDecimal.ZERO, BigDecimal::add).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);

        // Suavização para os períodos subsequentes (Wilder's smoothing)
        for (int i = period; i < gains.size(); i++) {
            avgGain = (avgGain.multiply(BigDecimal.valueOf(period - 1)).add(gains.get(i))).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
            avgLoss = (avgLoss.multiply(BigDecimal.valueOf(period - 1)).add(losses.get(i))).divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        }

        if (avgLoss.compareTo(BigDecimal.ZERO) == 0) {
            // Evita divisão por zero; se não há perdas, RSI é 100 (extremamente forte)
            result.addValue("rsiValue", BigDecimal.valueOf(100));
            result.setSignal(Signal.SELL); // Condição de sobrecompra extrema
            result.setDetails("RSI: 100.00 (Sobrecompra Extrema - Ausência de Perdas Médias)");
            return result;
        }
        
        BigDecimal rs = avgGain.divide(avgLoss, SCALE, RoundingMode.HALF_UP);
        BigDecimal rsi = BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(100).divide(BigDecimal.ONE.add(rs), SCALE, RoundingMode.HALF_UP));

        result.addValue("rsiValue", rsi.setScale(2, RoundingMode.HALF_UP));
        result.addValue("period", period);

        // Define o sinal baseado nos níveis de sobrecompra/sobrevenda
        if (rsi.compareTo(BigDecimal.valueOf(70)) > 0) {
            result.setSignal(Signal.SELL);
            result.setDetails(String.format("RSI: %.2f (Sobrecompra)", rsi));
        } else if (rsi.compareTo(BigDecimal.valueOf(30)) < 0) {
            result.setSignal(Signal.BUY);
            result.setDetails(String.format("RSI: %.2f (Sobrevenda)", rsi));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("RSI: %.2f (Neutra)", rsi));
        }
        return result;
    }
}
```

**2. Média Móvel Simples (SMA)**

Calcula a média dos preços de fechamento durante um período específico.

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import com.example.cryptanalyzer.model.Signal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@Service
public class SmaIndicator implements TechnicalIndicator {

    private static final String NAME = "SMA";
    private static final int DEFAULT_PERIOD = 20; // Período comum para SMA
    private static final int SCALE = 2; // Escala para o valor da SMA

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int period = params.containsKey("smaPeriod") ? Integer.parseInt(params.get("smaPeriod")) : DEFAULT_PERIOD;

        if (historicalData == null || historicalData.size() < period) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para calcular SMA com período " + period);
            return result;
        }

        // Pega os 'period' últimos preços de fechamento
        List<CandlestickData> relevantData = historicalData.subList(historicalData.size() - period, historicalData.size());
        BigDecimal sumOfCloses = relevantData.stream()
                                     .map(CandlestickData::getClose)
                                     .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal smaValue = sumOfCloses.divide(BigDecimal.valueOf(period), SCALE, RoundingMode.HALF_UP);
        BigDecimal currentPrice = historicalData.get(historicalData.size() - 1).getClose();

        result.addValue("smaValue", smaValue);
        result.addValue("currentPrice", currentPrice);
        result.addValue("period", period);

        // Sinal simples: preço atual vs SMA
        // (Estratégias mais complexas envolvem cruzamentos de SMAs de diferentes períodos)
        if (currentPrice.compareTo(smaValue) > 0) {
            result.setSignal(Signal.BUY); // Ou HOLD se já comprado
            result.setDetails(String.format("Preço (%.2f) acima da SMA(%d) (%.2f). Tendência de alta.", currentPrice, period, smaValue));
        } else if (currentPrice.compareTo(smaValue) < 0) {
            result.setSignal(Signal.SELL); // Ou HOLD se já vendido
            result.setDetails(String.format("Preço (%.2f) abaixo da SMA(%d) (%.2f). Tendência de baixa.", currentPrice, period, smaValue));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Preço (%.2f) igual à SMA(%d) (%.2f).", currentPrice, period, smaValue));
        }
        return result;
    }
}
```

**3. Suportes e Resistências (S/R)**

Identifica níveis de preço onde a tendência pode pausar ou reverter. Esta é uma implementação simplificada.

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import com.example.cryptanalyzer.model.Signal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SupportResistanceIndicator implements TechnicalIndicator {

    private static final String NAME = "Support/Resistance";
    private static final int DEFAULT_LOOKBACK_PERIOD = 20; // Nº de velas para identificar S/R
    // Distância percentual para considerar "próximo" ao suporte/resistência
    private static final BigDecimal PROXIMITY_PERCENTAGE = new BigDecimal("0.01"); // 1%

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public IndicatorResult analyze(List<CandlestickData> historicalData, Map<String, String> params) {
        IndicatorResult result = new IndicatorResult(getName());
        int lookbackPeriod = params.containsKey("srLookbackPeriod") ? Integer.parseInt(params.get("srLookbackPeriod")) : DEFAULT_LOOKBACK_PERIOD;

        if (historicalData == null || historicalData.size() < lookbackPeriod) {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails("Dados históricos insuficientes para S/R com lookback " + lookbackPeriod);
            return result;
        }

        List<CandlestickData> relevantData = historicalData.subList(historicalData.size() - lookbackPeriod, historicalData.size());

        BigDecimal supportLevel = Collections.min(relevantData.stream().map(CandlestickData::getLow).collect(Collectors.toList()));
        BigDecimal resistanceLevel = Collections.max(relevantData.stream().map(CandlestickData::getHigh).collect(Collectors.toList()));
        BigDecimal currentPrice = historicalData.get(historicalData.size() - 1).getClose();

        result.addValue("support", supportLevel.setScale(2, RoundingMode.HALF_UP));
        result.addValue("resistance", resistanceLevel.setScale(2, RoundingMode.HALF_UP));
        result.addValue("currentPrice", currentPrice.setScale(2, RoundingMode.HALF_UP));
        result.addValue("lookbackPeriod", lookbackPeriod);

        BigDecimal proximityToSupport = currentPrice.subtract(supportLevel);
        BigDecimal proximityToResistance = resistanceLevel.subtract(currentPrice);

        // Verifica se o preço está próximo ao suporte (dentro de PROXIMITY_PERCENTAGE acima do suporte)
        if (proximityToSupport.compareTo(BigDecimal.ZERO) >= 0 && 
            proximityToSupport.compareTo(supportLevel.multiply(PROXIMITY_PERCENTAGE)) <= 0) {
            result.setSignal(Signal.BUY);
            result.setDetails(String.format("Preço (%.2f) próximo ao Suporte (%.2f). Potencial Compra.", currentPrice, supportLevel));
        } 
        // Verifica se o preço está próximo à resistência (dentro de PROXIMITY_PERCENTAGE abaixo da resistência)
        else if (proximityToResistance.compareTo(BigDecimal.ZERO) >= 0 &&
                   proximityToResistance.compareTo(resistanceLevel.multiply(PROXIMITY_PERCENTAGE)) <= 0) {
            result.setSignal(Signal.SELL);
            result.setDetails(String.format("Preço (%.2f) próximo à Resistência (%.2f). Potencial Venda.", currentPrice, resistanceLevel));
        } else {
            result.setSignal(Signal.NEUTRAL);
            result.setDetails(String.format("Preço (%.2f) entre Suporte (%.2f) e Resistência (%.2f).", currentPrice, supportLevel, resistanceLevel));
        }
        return result;
    }
}
```

### Observações Adicionais:

* **Precisão com `BigDecimal`**: Usei `BigDecimal` para cálculos financeiros, o que é crucial para evitar erros de arredondamento de `double` ou `float`. A `SCALE` define o número de casas decimais.
* **Tratamento de Erros**: Adicionei verificações básicas para dados insuficientes. Em produção, um tratamento de erro mais robusto seria necessário.
* **Configuração de Parâmetros**: O `Map<String, String> params` permite flexibilidade para configurar cada indicador através da API (ex: diferentes períodos para RSI ou MAs).
* **Sinal de "Valor"**:
    * Para RSI e SMA, o "valor de compra/venda" é tipicamente o preço de mercado no momento em que o sinal (BUY/SELL) é gerado.
    * Para S/R, os próprios níveis de suporte e resistência podem ser considerados "valores" de interesse para entrada ou saída. O `IndicatorResult` armazena esses níveis no mapa `values`.
* **Complexidade dos Sinais**: As lógicas de sinal aqui são simplificadas. Em cenários reais, você pode querer:
    * Cruzamento de duas SMAs (ex: SMA(9) cruzando SMA(21)).
    * Divergências no RSI.
    * Confirmação de rompimento de S/R.
* **Orquestração**: Um serviço orquestrador (ex: `AnalysisOrchestratorService`) seria responsável por:
    1.  Injetar uma lista de todas as implementações de `TechnicalIndicator`.
    2.  Chamar o método `analyze()` de cada uma.
    3.  Coletar os `IndicatorResult`.
    4.  Possivelmente, aplicar uma lógica para consolidar os sinais (ex: votação, ponderação) e gerar uma recomendação final com um "valor de compra/venda" baseado no preço atual e nos sinais.

Estas classes fornecem uma base sólida para seus algoritmos. Você pode expandi-las, adicionar mais algoritmos (MACD, Bandas de Bollinger, etc.) e refinar a lógica de sinalização conforme necessário.

Claro! Vamos desenvolver as classes para os algoritmos MACD (Moving Average Convergence Divergence) e Bandas de Bollinger. Estes são indicadores muito populares e úteis na análise técnica.

Manteremos a mesma estrutura de DTOs (`CandlestickData`, `IndicatorResult`, `Signal`) e a interface `TechnicalIndicator` da resposta anterior.

### Funções Auxiliares Comuns (Potenciais)

Para indicadores como MACD, precisamos calcular a Média Móvel Exponencial (EMA). Poderíamos ter uma classe utilitária para isso, mas para simplificar, vou incluir o cálculo da EMA dentro da classe do MACD ou como um método estático auxiliar se for usado por múltiplos indicadores de forma idêntica.

#### Cálculo da EMA (Média Móvel Exponencial)

A EMA dá mais peso aos preços recentes.
Fórmula: `EMA_hoje = (Preço_Fechamento_hoje * Multiplicador) + EMA_ontem * (1 - Multiplicador)`
Onde `Multiplicador = Smoothing / (1 + Período)`. Comumente, `Smoothing = 2`.
A primeira EMA de uma série pode ser uma SMA do mesmo período.

---

### 1. MACD (Moving Average Convergence Divergence)

O MACD é um indicador de momento que segue tendências e mostra a relação entre duas médias móveis exponenciais de preços.

* **Linha MACD**: EMA(período curto) - EMA(período longo)
* **Linha de Sinal**: EMA(período da linha de sinal) da Linha MACD
* **Histograma MACD**: Linha MACD - Linha de Sinal

Sinais comuns:
* Cruzamento da Linha MACD sobre a Linha de Sinal (compra).
* Cruzamento da Linha MACD abaixo da Linha de Sinal (venda).
* Cruzamento da Linha MACD sobre a linha zero (momentum de alta).
* Cruzamento da Linha MACD abaixo da linha zero (momentum de baixa).

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import com.example.cryptanalyzer.model.Signal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
```

---

### 2. Bandas de Bollinger (Bollinger Bands)

As Bandas de Bollinger consistem em:
* Uma Média Móvel Simples (SMA) no meio (Banda Média).
* Uma Banda Superior (SMA + K * desvio padrão).
* Uma Banda Inferior (SMA - K * desvio padrão).
(K é tipicamente 2)

Sinais comuns:
* Preço tocando a Banda Inferior (potencial compra).
* Preço tocando a Banda Superior (potencial venda).
* "Squeeze" (Bandas estreitas) pode preceder alta volatilidade.

```java
package com.example.cryptanalyzer.service.indicator;

import com.example.cryptanalyzer.model.CandlestickData;
import com.example.cryptanalyzer.dto.IndicatorResult;
import com.example.cryptanalyzer.model.Signal;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
```

**Observação sobre `BigDecimal.sqrt()`**:
A classe `BigDecimal` não possui um método `sqrt()` direto até o Java 9 (onde `sqrt(MathContext)` foi adicionado). Se você estiver usando Java 8 ou anterior, precisará:
1.  Converter para `double`, calcular `Math.sqrt()`, e converter de volta para `BigDecimal` (como fiz no exemplo, o que pode perder alguma precisão, mas é frequentemente aceitável).
2.  Usar uma biblioteca de terceiros como Apache Commons Math.
3.  Implementar um algoritmo de raiz quadrada para `BigDecimal` (ex: método de Newton-Raphson).

Para a maioria dos casos em indicadores financeiros, a conversão para `double` para `sqrt` e de volta é uma aproximação razoável, mas esteja ciente da potencial perda de precisão se altíssima precisão for mandatória em todos os passos.

---

### O que achar mais necessário?

Além dos já mencionados, outros indicadores úteis incluem:

* **Oscilador Estocástico (Stochastic Oscillator)**: Similar ao RSI, compara o preço de fechamento de um ativo com seu range de preços durante um período. Bom para identificar condições de sobrecompra/sobrevenda.
* **On-Balance Volume (OBV)**: Um indicador de momentum baseado em volume. Mede a pressão de compra e venda.
* **Ichimoku Cloud (Nuvem de Ichimoku)**: Um indicador mais complexo que fornece múltiplos pontos de dados (níveis de suporte/resistência, direção da tendência, sinais de momentum).
* **Average True Range (ATR)**: Mede a volatilidade do mercado. Útil para definir stop-loss ou para entender a faixa de movimento esperada.

A escolha de quais adicionar depende da estratégia de negociação que se deseja modelar. Para uma aplicação que visa "maximizar a precisão", ter uma combinação de indicadores de tendência, momentum, volatilidade e volume pode ser benéfico. O desafio maior reside em como combinar os sinais desses diversos indicadores de forma eficaz.

Estas implementações de MACD e Bandas de Bollinger devem fornecer uma boa continuação para o seu projeto!

