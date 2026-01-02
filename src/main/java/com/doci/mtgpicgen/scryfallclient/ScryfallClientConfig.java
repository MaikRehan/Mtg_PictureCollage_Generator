package com.doci.mtgpicgen.scryfallclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@Getter
@Setter
public class ScryfallClientConfig {

    @Value("${scryfall-user-agent}")
    private String userAgent;

    @Value("${scryfall-accept-values}")
    private String acceptValues;



    @Bean
    WebClient scryfallWebClient() {

        return WebClient.builder()
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(configurer -> configurer
                                .defaultCodecs()
                                .maxInMemorySize(16 * 1024 * 1024)) // Set limit to 16MB
                        .build())
                .baseUrl("https://api.scryfall.com")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, acceptValues)
                .build();
    }
}
