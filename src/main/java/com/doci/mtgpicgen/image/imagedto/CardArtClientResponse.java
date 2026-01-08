package com.doci.mtgpicgen.image.imagedto;

import java.awt.image.BufferedImage;
import java.util.List;

public class  CardArtClientResponse {

    private List<BufferedImage> images;
    private int targetWidth;
    private int targetHeight;

    public CardArtClientResponse(List<BufferedImage> images, int targetWidth, int targetHeight) {
        this.images = images;
        this.targetWidth = targetWidth;
        this.targetHeight = targetHeight;
    }

    public List<BufferedImage> getImages() {
        return images;
    }

    public int getTargetWidth() {
        return targetWidth;
    }

    public int getTargetHeight() {
        return targetHeight;
    }
}
