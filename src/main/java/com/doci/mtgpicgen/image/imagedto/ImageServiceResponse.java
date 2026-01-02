package com.doci.mtgpicgen.image.imagedto;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;

@Getter
@Setter
public class ImageServiceResponse {
    private String message;
    private String collageImageUri;

    public ImageServiceResponse(BufferedImage collageImage, String message) {
        this.message = message;
        this.collageImageUri = collageImage != null ? "collage_image_uri_placeholder" : null;
    }
}
