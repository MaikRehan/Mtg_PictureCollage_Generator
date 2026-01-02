package com.doci.mtgpicgen.image.imagedto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CardArtUrlList {

    private List<String> artUrls;

    public void addArtUrl(String url) {
        this.artUrls.add(url);
    }
}
