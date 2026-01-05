package com.doci.mtgpicgen.controller;

import com.doci.mtgpicgen.controller.dto.CollageRequest;
import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.service.Service;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;

@RestController
@RequestMapping("/api/scryfall")
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(value="/collage")
    public CollageResponse collage(@RequestBody CollageRequest request) throws IOException {
        return service.getCardCollage(request);
    }

}