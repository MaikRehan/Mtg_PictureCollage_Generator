package com.doci.mtgpicgen.scryfallclient.clientdto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ScryfallResponse {
    private List<ScryfallCard> cardList;
    private Integer total_cards;
}
