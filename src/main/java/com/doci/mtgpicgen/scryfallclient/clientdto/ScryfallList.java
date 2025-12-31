package com.doci.mtgpicgen.scryfallclient.clientdto;

import lombok.Data;

import java.util.List;

@Data
public class ScryfallList<T>{
    private boolean has_more;
    private String next_page;
    private List<T> data;
    private Integer total_cards;
}

