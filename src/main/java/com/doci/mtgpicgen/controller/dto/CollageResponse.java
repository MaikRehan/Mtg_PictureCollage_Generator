package com.doci.mtgpicgen.controller.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollageResponse {
    private String message;
    private String collageImageUri;

    public CollageResponse(String message, String collageImageUri) {
        this.message = message;
        this.collageImageUri = collageImageUri;
    }
}
