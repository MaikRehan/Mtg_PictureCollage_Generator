package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Scryfall does not provide the images themselves, but URLs to the images. This Client downloads the images from
 * the URLs and returns them as a List of BufferedImages.
 */
@Component
public class CardArtClient {

    public List<BufferedImage> downloadCardArt(List<ScryfallCard> cardList) throws IOException {
        List<BufferedImage> imageList = new ArrayList<>();

        for (ScryfallCard card : cardList) {
            URL artUrl = card.getImage_uris().getArt_crop();
            if (artUrl != null) {
                BufferedImage image = ImageIO.read(artUrl);
                if (image == null) {
                    throw new IllegalArgumentException("Kein Bild-URL gefunden");
                }
                imageList.add(image);
            }
        }
        return imageList;
    }
}
