package com.doci.mtgpicgen.controller;

import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallCard;
import com.doci.mtgpicgen.scryfallclient.clientdto.ScryfallList;
import com.doci.mtgpicgen.service.ScryfallService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/scryfall")
public class Controller {

    private final ScryfallService scryfallService;

    public Controller(ScryfallService scryfallService) {
        this.scryfallService = scryfallService;
    }

    /**
     * GET /api/scryfall/gates
     */
    @GetMapping("/gates")
    public ScryfallList<ScryfallCard> getAllGates() {
        return scryfallService.getAllGates();
    }
}