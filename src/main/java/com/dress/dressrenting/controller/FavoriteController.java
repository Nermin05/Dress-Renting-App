package com.dress.dressrenting.controller;

import com.dress.dressrenting.dto.response.ProductResponseDto;
import com.dress.dressrenting.service.FavoriteService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/favorites")
@RequiredArgsConstructor
@Tag(name = "Favorite Management")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @GetMapping
    public ResponseEntity<List<ProductResponseDto>> getAll() {
        return ResponseEntity.ok(favoriteService.getFavoriteList());
    }

    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestParam String productCode) {
        favoriteService.addFavorite(productCode);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping()
    public ResponseEntity<Void> removeFromFavorite(@RequestParam String productCode) {
        favoriteService.removeFromFavorite(productCode);
        return ResponseEntity.noContent().build();
    }
}
