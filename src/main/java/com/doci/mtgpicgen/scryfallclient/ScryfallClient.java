package com.doci.mtgpicgen.scryfallclient;

import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Component
public class ScryfallClient {

    private final WebClient webClient;

    public ScryfallClient(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     Fetch Logic
     */

    public ScryfallResponse fetchAllCards(String query) {
        List<ScryfallCard> result = new ArrayList<>();
        URI uri = buildInitialUri(query);

        while (uri != null) {
            ScryfallList<ScryfallCard> page = fetchPage(uri);

            if (page != null && page.getData() != null) {
                result.addAll(page.getData());
                uri = getNextPageUri(page);
                waitIfNeeded(uri);
            } else {
                uri = null;
            }
        }

        return buildResponse(result);
    }

    private URI buildInitialUri(String query) {
        return UriComponentsBuilder.fromPath("/cards/search")
                .queryParam("q", query)
   //             .queryParam("unique", "art")
                .build()
                .toUri();
    }

    /**
     * Fetch a single page of cards.
     */
    private ScryfallList<ScryfallCard> fetchPage(URI uri) {
        final URI finalUri = uri;

        return webClient.get()
                .uri(uriBuilder -> finalUri.isAbsolute()
                        ? finalUri
                        : uriBuilder.path(finalUri.getPath()).query(finalUri.getQuery()).build())
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<ScryfallList<ScryfallCard>>() {})
                .onErrorResume(org.springframework.web.reactive.function.client.WebClientResponseException.NotFound.class,
                        e -> reactor.core.publisher.Mono.empty())
                .block();
    }

    /**
     Scryfall sends the response objects paginated in packages.
     */
    private URI getNextPageUri(ScryfallList<ScryfallCard> page) {
        if (page.isHas_more() && page.getNext_page() != null) {
            return URI.create(page.getNext_page());
        }
        return null;
    }

    /**
     Scryfall wants requests to be 100ms apart as of their fair use guidelines.
     */
    private void waitIfNeeded(URI nextUri) {
        if (nextUri != null) {
            try {
                Thread.sleep(100); // Scryfall API Guideline
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private ScryfallResponse buildResponse(List<ScryfallCard> cards) {
        ScryfallResponse response = new ScryfallResponse();
        response.setCardList(cards);
        response.setTotal_cards(cards.size());
        return response;
    }
}
