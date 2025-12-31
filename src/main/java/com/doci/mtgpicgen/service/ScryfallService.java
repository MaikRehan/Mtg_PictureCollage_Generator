package com.doci.mtgpicgen.service;

import com.doci.mtgpicgen.scryfallclient.ScryfallClient;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScryfallService {

    private final ScryfallClient scryfallClient;

    public ScryfallService(ScryfallClient scryfallClient) {
        this.scryfallClient = scryfallClient;
    }

    /**
     * Liefert alle Gate-Länder als Business-Ergebnis
     */
    public ScryfallList<ScryfallCard> getAllGates() {
        return scryfallClient.fetchAllGates();
    }
}