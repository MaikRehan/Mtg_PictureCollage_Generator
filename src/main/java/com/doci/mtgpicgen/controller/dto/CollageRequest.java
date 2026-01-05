package com.doci.mtgpicgen.controller.dto;

import com.doci.mtgpicgen.image.ArrangementMethod;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CollageRequest {
    private String query;
    private Integer collageWidth;
    private Integer collageHeight;
    private Integer numberOfColumns;
    private ArrangementMethod arrangementMethod;
    private Integer borderSize;
}
