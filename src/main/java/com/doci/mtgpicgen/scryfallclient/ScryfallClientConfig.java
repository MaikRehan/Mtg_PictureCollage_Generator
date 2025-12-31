package com.doci.mtgpicgen.scryfallclient;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
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
                .baseUrl("https://api.scryfall.com")
                .defaultHeader(HttpHeaders.USER_AGENT, userAgent)
                .defaultHeader(HttpHeaders.ACCEPT, acceptValues)
                .build();
    }
}
