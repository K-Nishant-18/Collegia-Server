package com.example.demo.service;

import com.example.demo.entity.Material;
import com.example.demo.repository.MaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaterialService {
    @Autowired
    private MaterialRepository materialRepository;

    public List<Material> getAllMaterials() {
        return materialRepository.findAll();
    }

    public List<Material> searchMaterials(String category, String query) {
        if (query == null) query = "";
        if (category == null || category.equals("All")) category = null;
        return materialRepository.searchMaterials(category, query);
    }

    public List<Material> getUserMaterials(String userId) {
        return materialRepository.findByUploadedBy(userId);
    }

    public Material createMaterial(Material material) {
        return materialRepository.save(material);
    }

    public Material updateMaterial(Long id, Material material) {
        if (materialRepository.existsById(id)) {
            material.setId(id);
            return materialRepository.save(material);
        }
        throw new RuntimeException("Material not found with id: " + id);
    }

    public String deleteMaterial(Long id) {
        Material material = materialRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Material not found with id: " + id));
        String pdfPath = material.getPdfPath();
        materialRepository.deleteById(id);
        return pdfPath;
    }
}