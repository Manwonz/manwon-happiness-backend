package com.manwon.happiness.place.controller;

import com.manwon.happiness.place.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/places")
public class PlaceController {

    private final PlaceService placeService;

    @PostMapping("/import")
    public String importCsv(@RequestParam String path) {
        placeService.importFromCsv(path);
        return "CSV Import 완료";
    }
}