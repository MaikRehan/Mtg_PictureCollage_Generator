package com.doci.mtgpicgen.service;

import com.doci.mtgpicgen.controller.dto.CollageRequest;
import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.image.ImageService;
import com.doci.mtgpicgen.image.imagedto.ImageServiceRequest;
import com.doci.mtgpicgen.image.imagedto.ImageServiceResponse;
import com.doci.mtgpicgen.scryfallclient.ScryfallClient;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;

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


    public CollageResponse getCardCollage(CollageRequest request) throws IOException {
        ScryfallResponse scryfallResponse = scryfallClient.fetchAllCards(request.getQuery());
        System.out.println("Anzahl der Karten: " + scryfallResponse.getTotal_cards());
        ImageServiceRequest imageServiceRequest = mapToImageServiceRequest(request, scryfallResponse);
        imageServiceRequest.setCardList(dropExcessCards(imageServiceRequest));
        ImageServiceResponse imageServiceResponse = imageService.createCollage(imageServiceRequest);

        return new CollageResponse(imageServiceResponse.getCollageImageURL(),imageServiceResponse.getMessage() );
    }


    private ImageServiceRequest mapToImageServiceRequest (CollageRequest collageRequest, ScryfallResponse scryfallResponse) {
        ImageServiceRequest imageServiceRequest = new ImageServiceRequest();
        imageServiceRequest.setCardList(scryfallResponse.getCardList());
        imageServiceRequest.setCollageNumberOfColumns(collageRequest.getNumberOfColumns());
        imageServiceRequest.setCollageWidth(collageRequest.getCollageWidth());
        imageServiceRequest.setCollageHeight(collageRequest.getCollageHeight());
        imageServiceRequest.setTotalCards(scryfallResponse.getTotal_cards());
        imageServiceRequest.setArrangementMethod(collageRequest.getArrangementMethod());
        imageServiceRequest.setBorderSize(collageRequest.getBorderSize());
        return imageServiceRequest;
    }
    private List<ScryfallCard> dropExcessCards(ImageServiceRequest imageServiceRequest) {
        int cardCount = imageServiceRequest.getCardList().size();
        int columns = imageServiceRequest.getCollageNumberOfColumns();

        if (columns <= 0) {
            return imageServiceRequest.getCardList();
        }
        int remainder = cardCount % columns;

        if (remainder != 0) {
            imageServiceRequest.getCardList().subList(cardCount - remainder, cardCount).clear();
        }
        return imageServiceRequest.getCardList();
    }
}