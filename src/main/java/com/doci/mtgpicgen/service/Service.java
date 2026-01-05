package com.doci.mtgpicgen.service;

import com.doci.mtgpicgen.controller.dto.CollageRequest;
import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.image.ImageService;
import com.doci.mtgpicgen.image.imagedto.ImageServiceRequest;
import com.doci.mtgpicgen.image.imagedto.ImageServiceResponse;
import com.doci.mtgpicgen.scryfallclient.ScryfallClient;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;

import java.io.IOException;

@org.springframework.stereotype.Service
public class Service {

    private final ScryfallClient scryfallClient;
    private final ImageService imageService;

    public Service(ScryfallClient scryfallClient, ImageService imageService) {
        this.scryfallClient = scryfallClient;
        this.imageService = imageService;
    }

    public CollageResponse getCardCollage(CollageRequest request) throws IOException {
        ScryfallResponse scryfallResponse = scryfallClient.fetchAllCards(request.getQuery());

        ImageServiceRequest imageServiceRequest = mapToImageServiceRequest(request, scryfallResponse);
        ImageServiceResponse imageServiceResponse = imageService.createCollage(imageServiceRequest);

        return new CollageResponse(imageServiceResponse.getCollageImageURL(),imageServiceResponse.getMessage() );
    }

// ---------OLD TEST METHODS BELOW---------
//    public CollageResponse getGateCollage() throws IOException {
//        List<ScryfallCard> cardList = scryfallClient.fetchAllGates().getCardList();
//        imageService.createCollage(cardList);
//        return null;
//    }
//
//    public CollageResponse getDarksteelCollage() throws IOException {
//        List<ScryfallCard> cardList = scryfallClient.fetchAllDarksteel().getCardList();
//        imageService.createCollage(cardList);
//        return null;
//    }

    private ImageServiceRequest mapToImageServiceRequest (CollageRequest collageRequest, ScryfallResponse scryfallResponse) {
        ImageServiceRequest imageServiceRequest = new ImageServiceRequest();
        imageServiceRequest.setCardList(scryfallResponse.getCardList());
        imageServiceRequest.setCollageNumberOfColumns(collageRequest.getNumberOfColumns());
        imageServiceRequest.setCollageWidth(collageRequest.getCollageWidth());
        imageServiceRequest.setCollageHeight(collageRequest.getCollageHeight());
        imageServiceRequest.setTotalCards(scryfallResponse.getTotal_cards());
        imageServiceRequest.setArrangementMethod(collageRequest.getArrangementMethod());
        return imageServiceRequest;

    }



}