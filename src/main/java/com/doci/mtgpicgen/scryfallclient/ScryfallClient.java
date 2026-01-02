package com.doci.mtgpicgen.scryfallclient;


import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScryfallClient {

    private final WebClient webClient;

    public ScryfallClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * Lädt alle Gate-Länder aus Scryfall (pagination + required headers)
     */


    public ScryfallResponse fetchAllGates() {
        ScryfallResponse response = new ScryfallResponse();
        List<ScryfallCard> result = new ArrayList<>();

        String fullQuery = "type:land type:gate game:paper prefer:best";

        // Initialer Aufruf mit relativem Pfad (wird mit baseUrl kombiniert)
        URI uri = UriComponentsBuilder.fromPath("/cards/search")
                .queryParam("q", fullQuery)
                .queryParam("unique", "art")
                .build()
                .toUri();

        while (uri != null) {
            final URI finalUri = uri;

            ScryfallList<ScryfallCard> page = webClient.get()
                    .uri(uriBuilder -> finalUri.isAbsolute() ? finalUri : uriBuilder.path(finalUri.getPath()).query(finalUri.getQuery()).build())
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ScryfallList<ScryfallCard>>() {})
                    .block();

            if (page != null && page.getData() != null) {
                result.addAll(page.getData());

                // Scryfall gibt eine absolute URL in 'next_page' zurück
                uri = (page.isHas_more() && page.getNext_page() != null)
                        ? URI.create(page.getNext_page())
                        : null;

                if (uri != null) {
                    try {
                        Thread.sleep(100); // Höfliches Warten laut Scryfall API Guideline
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            } else {
                uri = null;
            }
        }

        response.setCardList(result);
        response.setTotal_cards(result.size());
        return response;
    }


}
