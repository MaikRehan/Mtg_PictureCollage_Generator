package com.doci.mtgpicgen.controller;

import com.doci.mtgpicgen.controller.dto.CollageRequest;
import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallResponse;
import com.doci.mtgpicgen.service.Service;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/scryfall")
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    /**
     * GET /api/scryfall/gates
     */
    @GetMapping("/gates")
    public ScryfallResponse getAllGates() throws IOException {
        return service.getAllGates();
    }
    @PostMapping(value="/collage")
    public CollageResponse collage(@RequestBody CollageRequest request) {
        return service.getGateCollage();
    }
}