package com.doci.mtgpicgen.scryfallclient.clientdto;

import lombok.Getter;
import lombok.Setter;

import java.net.URL;

@Getter
@Setter
public class ScryfallImageUris {

    private URL small;
    private URL normal;
    private URL large;
    private URL png;
    private URL art_crop;
    private URL border_crop;
}
