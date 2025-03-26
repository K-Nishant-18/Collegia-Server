package com.example.demo.controller;

import com.example.demo.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@CrossOrigin(origins = "http://localhost:3000")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/{userId}")
    public List<Long> getFavorites(@PathVariable String userId) {
        return favoriteService.getFavorites(userId);
    }

    @PostMapping
    public void toggleFavorite(@RequestParam String userId, @RequestParam Long materialId) {
        favoriteService.toggleFavorite(userId, materialId);
    }
}