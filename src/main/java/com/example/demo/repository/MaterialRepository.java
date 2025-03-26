package com.example.demo.repository;

import com.example.demo.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    @Query("SELECT m FROM Material m WHERE " +
            "(:category IS NULL OR m.category = :category) AND " +
            "(m.title LIKE %:query% OR m.description LIKE %:query% OR " +
            "EXISTS (SELECT t FROM m.tags t WHERE t LIKE %:query%)) " +
            "ORDER BY m.createdAt DESC") // Sort by createdAt descending
    List<Material> searchMaterials(@Param("category") String category, @Param("query") String query);

    List<Material> findByUploadedBy(String uploadedBy);

    @Override
    @Query("SELECT m FROM Material m ORDER BY m.createdAt DESC")
    List<Material> findAll();
}