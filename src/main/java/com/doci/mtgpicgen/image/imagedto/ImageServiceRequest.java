package com.doci.mtgpicgen.image.imagedto;

import com.doci.mtgpicgen.image.ArrangementMethod;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ImageServiceRequest {
    private List<ScryfallCard> cardList;
    private int collageNumberOfColumns;
    private int collageWidth;
    private int collageHeight;
    private int totalCards;
    private ArrangementMethod arrangementMethod;
    private int borderSize;
}
