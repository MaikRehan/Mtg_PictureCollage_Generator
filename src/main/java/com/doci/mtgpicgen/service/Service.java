package com.doci.mtgpicgen.service;

import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.image.ImageService;
import com.doci.mtgpicgen.scryfallclient.ScryfallClient;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.List;

@org.springframework.stereotype.Service
public class Service {

    private final ScryfallClient scryfallClient;
    private final ImageService imageService;

    public Service(ScryfallClient scryfallClient, ImageService imageService) {
        this.scryfallClient = scryfallClient;
        this.imageService = imageService;
    }


    public ScryfallResponse getAllGates() {
        return scryfallClient.fetchAllGates();
    }
    public CollageResponse getGateCollage() throws IOException {
        List<ScryfallCard> cardList = scryfallClient.fetchAllGates().getCardList();
        imageService.createCollage(cardList);
        return null;
    }

    public CollageResponse getDarksteelCollage() throws IOException {
        List<ScryfallCard> cardList = scryfallClient.fetchAllDarksteel().getCardList();
        imageService.createCollage(cardList);
        return null;
    }
}