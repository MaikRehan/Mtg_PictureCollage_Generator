package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.image.imagedto.ImageServiceResponse;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static com.doci.mtgpicgen.image.CollageGenerator.ArrangementMethod.*;

@Service
public class ImageService {

    private final CardArtClient cardArtClient;

    public ImageService(CardArtClient cardArtClient) {
        this.cardArtClient = cardArtClient;
    }


    public ImageServiceResponse createCollage(List<ScryfallCard> cardList) throws java.io.IOException {
        CollageGenerator collageGenerator = new CollageGenerator();
        String name = "collage_";
        List<BufferedImage> images = downloadCardArt(cardList);


        BufferedImage collageImageDia = collageGenerator.generateCollage(images, DIAGONAL);  // Collage erstellen
        saveImage(collageImageDia, name + "diagonal");
        // Collage speichern
        BufferedImage collageImageHilbert = collageGenerator.generateCollage(images, HILBERT);  // Collage erstellen
        saveImage(collageImageHilbert, name + "Hilbert");

        BufferedImage collageImageSOM = collageGenerator.generateCollage(images, SOM);  // Collage erstellen
        saveImage(collageImageSOM, name + "SOM");

        BufferedImage collageImageSnake = collageGenerator.generateCollage(images, SNAKE);  // Collage erstellen
        saveImage(collageImageSnake, name + "Snake");

        BufferedImage collageImageLinear = collageGenerator.generateCollage(images, LINEAR);  // Collage erstellen
        saveImage(collageImageLinear, name + "Linear");

        String message = "Collage erfolgreich erstellt";


        return new ImageServiceResponse(collageImageDia, message);
    }


    private List<BufferedImage> downloadCardArt(List<ScryfallCard> cardList) throws java.io.IOException {
        return cardArtClient.downloadCardArt(cardList);
    }


    private void saveImage(BufferedImage image, String name) throws java.io.IOException {
        Path directory = Path.of("target", "generated");// Directory, in der die Datei abgespeichert werden soll
        if (!Files.exists(directory)) {
            Files.createDirectories(directory);                     //Directory erstellen, falls noch nicht vorhanden
        }
        File outputFile = directory.resolve(name + ".jpg").toFile();
        ImageIO.write(image, "jpg", outputFile);
        System.out.println("Collage gespeichert: " + outputFile.getAbsolutePath() + " --- Farbverlauf "+ name);
    }



}
