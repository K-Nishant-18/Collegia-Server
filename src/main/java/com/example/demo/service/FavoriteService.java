package com.example.demo.service;

import com.example.demo.entity.Favorite;
import com.example.demo.repository.FavoriteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private MaterialService materialService;

    public List<Long> getFavorites(String userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(Favorite::getMaterialId)
                .collect(Collectors.toList());
    }

    public void toggleFavorite(String userId, Long materialId) {
        if (favoriteRepository.existsByUserIdAndMaterialId(userId, materialId)) {
            favoriteRepository.deleteByUserIdAndMaterialId(userId, materialId);
        } else {
            Favorite favorite = new Favorite();
            favorite.setUserId(userId);
            favorite.setMaterialId(materialId);
            favoriteRepository.save(favorite);
        }
    }
}