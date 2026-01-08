package com.doci.mtgpicgen.image;

import com.doci.mtgpicgen.image.imagedto.CardArtClientResponse;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
public class CardArtClient {

    private static final double TARGET_ASPECT_RATIO = 626.0 / 457.0; // ~1.37

    public CardArtClientResponse downloadCardArt(List<ScryfallCard> cardList) throws IOException {
        List<BufferedImage> rawImages = new ArrayList<>();

        // 1. Alle Bilder herunterladen
        for (ScryfallCard card : cardList) {
            if (card.getImage_uris() == null) {
                continue; // Karten ohne Bild-URLs überspringen
            }
            URL artUrl = card.getImage_uris().getArt_crop();
            if (artUrl != null) {
                BufferedImage image = ImageIO.read(artUrl);
                if (image != null) {
                    rawImages.add(image);
                }
            }
        }

        if (rawImages.isEmpty()) {
            return new CardArtClientResponse(new ArrayList<>(), 0, 0);
        }

        // 2. Kleinste Dimensionen ermitteln (nach Aspect-Ratio-Korrektur)
        int minWidth = Integer.MAX_VALUE;
        int minHeight = Integer.MAX_VALUE;

        for (BufferedImage img : rawImages) {
            Dimension croppedSize = calculateCroppedSize(img.getWidth(), img.getHeight());
            minWidth = Math.min(minWidth, croppedSize.width);
            minHeight = Math.min(minHeight, croppedSize.height);
        }

        // 3. Alle Bilder zuschneiden und skalieren
        List<BufferedImage> processedImages = new ArrayList<>();
        for (BufferedImage img : rawImages) {
            BufferedImage processed = cropAndScale(img, minWidth, minHeight);
            processedImages.add(processed);
        }

        return new CardArtClientResponse(processedImages, minWidth, minHeight);
    }

    /**
     * Berechnet die Größe nach dem Zuschneiden auf das Ziel-Seitenverhältnis
     */
    private Dimension calculateCroppedSize(int width, int height) {
        double currentRatio = (double) width / height;

        if (currentRatio > TARGET_ASPECT_RATIO) {
            // Bild ist zu breit -> Breite anpassen
            int newWidth = (int) (height * TARGET_ASPECT_RATIO);
            return new Dimension(newWidth, height);
        } else if (currentRatio < TARGET_ASPECT_RATIO) {
            // Bild ist zu hoch -> Höhe anpassen
            int newHeight = (int) (width / TARGET_ASPECT_RATIO);
            return new Dimension(width, newHeight);
        }
        return new Dimension(width, height);
    }

    /**
     * Schneidet das Bild auf das Ziel-Seitenverhältnis zu und skaliert auf die Zielgröße
     */
    private BufferedImage cropAndScale(BufferedImage original, int targetWidth, int targetHeight) {
        // 1. Auf korrektes Seitenverhältnis zuschneiden
        BufferedImage cropped = cropToAspectRatio(original);

        // 2. Auf Zielgröße skalieren
        BufferedImage scaled = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = scaled.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(cropped, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return scaled;
    }

    /**
     * Schneidet das Bild gleichmäßig von beiden Seiten zu
     */
    private BufferedImage cropToAspectRatio(BufferedImage original) {
        int width = original.getWidth();
        int height = original.getHeight();
        double currentRatio = (double) width / height;

        int cropX = 0, cropY = 0, cropWidth = width, cropHeight = height;

        if (currentRatio > TARGET_ASPECT_RATIO) {
            // Zu breit -> links und rechts gleichmäßig abschneiden
            cropWidth = (int) (height * TARGET_ASPECT_RATIO);
            cropX = (width - cropWidth) / 2;
        } else if (currentRatio < TARGET_ASPECT_RATIO) {
            // Zu hoch -> oben und unten gleichmäßig abschneiden
            cropHeight = (int) (width / TARGET_ASPECT_RATIO);
            cropY = (height - cropHeight) / 2;
        }

        return original.getSubimage(cropX, cropY, cropWidth, cropHeight);
    }
}
