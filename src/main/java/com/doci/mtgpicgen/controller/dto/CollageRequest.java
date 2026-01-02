package com.doci.mtgpicgen.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CollageRequest {
    private String query;
    private Integer collageWidth;
    private Integer collageHeight;

}
