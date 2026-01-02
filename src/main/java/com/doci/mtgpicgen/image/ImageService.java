package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.image.imagedto.ImageServiceResponse;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

@Service
public class ImageService {

    private final CardArtClient cardArtClient;

    public ImageService(CardArtClient cardArtClient) {
        this.cardArtClient = cardArtClient;
    }
    public ImageServiceResponse createCollage(List<ScryfallCard> cardList) throws java.io.IOException {
        CollageGenerator collageGenerator = new CollageGenerator();

        List<BufferedImage> images = downloadCardArt(cardList);// Karten laden
        BufferedImage collageImage = collageGenerator.generateCollage(images); // Collage erstellen

        File outputFile = new File("C:/temp/collage.jpg");
        ImageIO.write(collageImage, "jpg", outputFile);
        System.out.println("Collage gespeichert: " + outputFile.getAbsolutePath());

        String message = "Collage erfolgreich erstellt";
        return new ImageServiceResponse(collageImage, message);
    }

    private List<BufferedImage> downloadCardArt(List<ScryfallCard> cardList) throws java.io.IOException {
        return cardArtClient.downloadCardArt(cardList);
    }



}
