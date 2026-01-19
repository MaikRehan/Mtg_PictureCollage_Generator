package com.doci.mtgpicgen.controller;

import com.doci.mtgpicgen.controller.dto.CollageRequest;
import com.doci.mtgpicgen.controller.dto.CollageResponse;
import com.doci.mtgpicgen.service.Service;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/scryfall")
@CrossOrigin(origins = "http://localhost:3000") // Erlaubt React-Frontend
public class Controller {

    private final Service service;

    public Controller(Service service) {
        this.service = service;
    }

    @PostMapping(value = "/collage")
    public CollageResponse collage(@RequestBody CollageRequest request) throws IOException {
        return service.getCardCollage(request);
    }

    @GetMapping(value = "/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<Resource> getImage(@RequestParam String path) {
        File file = new File(path);
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(resource);
    }
}