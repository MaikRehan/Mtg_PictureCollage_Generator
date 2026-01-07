package com.doci.mtgpicgen.controller.dto;

import com.doci.mtgpicgen.image.ArrangementMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollageRequest {
    private String query;
    private int collageWidth;
    private int collageHeight;
    private int numberOfColumns;
    private ArrangementMethod arrangementMethod;
    private int borderSize;
}
