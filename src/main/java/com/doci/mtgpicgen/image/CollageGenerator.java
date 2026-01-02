package com.doci.mtgpicgen.image;

import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

@Component
public class CollageGenerator {

    private static final int TILE_WIDTH = 200;
    private static final int TILE_HEIGHT = 150;
    private static final int COLUMNS = 5;

    public CollageGenerator() {
    }

    public BufferedImage generateCollage(List<BufferedImage> artList) {
        if (artList == null || artList.isEmpty()) {
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        }

        int rows = (int) Math.ceil((double) artList.size() / COLUMNS);
        int width = COLUMNS * TILE_WIDTH;
        int height = rows * TILE_HEIGHT;

        BufferedImage collage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = collage.createGraphics();

        // Hintergrund füllen
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, width, height);

        // Bilder zeichnen
        for (int i = 0; i < artList.size(); i++) {
            BufferedImage img = artList.get(i);
            int col = i % COLUMNS;
            int row = i / COLUMNS;
            int x = col * TILE_WIDTH;
            int y = row * TILE_HEIGHT;

            // Bild skaliert zeichnen
            g2d.drawImage(img, x, y, TILE_WIDTH, TILE_HEIGHT, null);
        }

        g2d.dispose();
        return collage;
    }
}
