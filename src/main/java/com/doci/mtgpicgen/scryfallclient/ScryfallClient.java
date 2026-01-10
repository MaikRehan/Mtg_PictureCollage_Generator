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
        result = deleteDefectiveCards(result);

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
    public List<ScryfallCard> deleteDefectiveCards(List<ScryfallCard> cards) {
        return cards.stream()
                .filter(card -> card.getId() == null || !DEFECTIVE_CARD_IDS.contains(card.getId()))
                .toList();
    }

    private static final List<String> DEFECTIVE_CARD_IDS = List.of(
            "8be6c40e-5669-417e-a655-b32b5f2bfd11",
            "3e574ee5-d33d-4054-9884-fdb5fe9d73bd",
            "7974535c-d99b-4d69-a21d-63359e40d385",
            "bbbecd1f-81ad-427e-a50d-64b9ec029c3c",
            "95315204-a5f7-4d20-bda3-957029da29fe",
            "f836071c-9ee3-4dc0-95b6-28d0c5de76f3",
            "7c71891b-9980-4b71-8301-e92288d33235",
            "5f1e5571-866b-4b19-b786-e57781353913",
            "490452a3-22fb-4c75-9bf2-5cd6d6719446",
            "c3b28bdb-e0d1-4d24-8199-8cd7ac12c30f",
            "75edde64-3589-424d-bfe1-714c4eaba019",
            "bdb63d4b-0bca-4b8e-bed0-0dc29ad0bac3",
            "db3036bd-95f0-403f-a6ba-b3b05f81a3c9",
            "b3344d18-e7b6-43ad-ab59-9ceaf49269f7",
            "614c9269-77c6-4cef-a987-8ef4c14fcebc"
    );

}
