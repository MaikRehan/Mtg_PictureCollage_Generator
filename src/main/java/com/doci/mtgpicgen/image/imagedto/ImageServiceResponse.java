package com.doci.mtgpicgen.image.imagedto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ImageServiceResponse {
    private String message;
    private String collageImageURL;

    public ImageServiceResponse(String collageImageURI, String message) {
        this.message = message;
        this.collageImageURL = collageImageURI;
    }
}
