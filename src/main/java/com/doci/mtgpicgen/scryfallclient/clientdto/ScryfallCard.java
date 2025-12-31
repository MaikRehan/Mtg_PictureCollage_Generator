package com.doci.mtgpicgen.scryfallclient.clientdto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScryfallCard{

    private String name;
    private String pictureUri;
    private ScryfallImageUris image_uris;
}

