package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.image.imagedto.ImageServiceRequest;
import com.doci.mtgpicgen.image.imagedto.ImageServiceResponse;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class ImageService {

    private final CardArtClient cardArtClient;
    private final ImageGenerator imageGenerator;

    public ImageService(CardArtClient cardArtClient, ImageGenerator imageGenerator) {
        this.cardArtClient = cardArtClient;
        this.imageGenerator = imageGenerator;
    }


    public ImageServiceResponse createCollage(ImageServiceRequest request) throws java.io.IOException {
        String name = "collage_";
        List<BufferedImage> images = downloadCardArt(request.getCardList());

        BufferedImage collageImage = imageGenerator.generateCollage(images, request);
        String message = "Collage erfolgreich erstellt";


        String collageImageURL = saveImage(collageImage, request.getArrangementMethod().getValue());

        return new ImageServiceResponse(message, collageImageURL);
    }


    private List<BufferedImage> downloadCardArt(List<ScryfallCard> cardList) throws java.io.IOException {
        return cardArtClient.downloadCardArt(cardList);
    }


    private String saveImage(BufferedImage image, String arrangementMethodMethod) throws java.io.IOException {
        Path directory = Path.of("target", "generated");// Directory, in der die Datei abgespeichert werden soll
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);                     //Directory erstellen, falls noch nicht vorhanden
        }
        File outputFile = directory.resolve( "collage_" + arrangementMethodMethod + ".jpg").toFile();
        ImageIO.write(image, "jpg", outputFile);
        System.out.println("Collage gespeichert: " + outputFile.getAbsolutePath() + " --- Methode: "+ arrangementMethodMethod);
        return outputFile.getAbsolutePath();
    }
}




