package com.example.demo.controller;

import com.example.demo.entity.Material;
import com.example.demo.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/materials")
@CrossOrigin(origins = "http://localhost:5173")
public class MaterialController {
    @Autowired
    private MaterialService materialService;

    private static final String UPLOAD_DIR = "uploads/";

    @GetMapping
    public List<Material> getAllMaterials() {
        return materialService.getAllMaterials();
    }

    @GetMapping("/search")
    public List<Material> searchMaterials(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query) {
        return materialService.searchMaterials(category, query);
    }

    @GetMapping("/user/{userId}")
    public List<Material> getUserMaterials(@PathVariable String userId) {
        return materialService.getUserMaterials(userId);
    }

    @PostMapping
    public Material createMaterial(
            @RequestPart("title") String title,
            @RequestPart("category") String category,
            @RequestPart("date") String date,
            @RequestPart("tags") String tags,
            @RequestPart(value = "url", required = false) String url,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @RequestPart("description") String description,
            @RequestParam("userId") String userId) throws IOException { // Add userId as a param
        Material material = new Material();
        material.setTitle(title);
        material.setCategory(category);
        material.setUploadedBy(userId); // Use authenticated userId
        material.setDate(date);
        material.setTags(Arrays.asList(tags.split(",")));
        material.setUrl(url != null ? url : "");
        material.setDescription(description);

        if (pdf != null && !pdf.isEmpty()) {
            String fileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, pdf.getBytes());
            material.setPdfPath("http://localhost:8080/" + UPLOAD_DIR + fileName);
        }

        return materialService.createMaterial(material);
    }

    @PutMapping("/{id}")
    public Material updateMaterial(
            @PathVariable Long id,
            @RequestPart("title") String title,
            @RequestPart("category") String category,
            @RequestPart("date") String date,
            @RequestPart("tags") String tags,
            @RequestPart(value = "url", required = false) String url,
            @RequestPart(value = "pdf", required = false) MultipartFile pdf,
            @RequestPart("description") String description,
            @RequestParam("userId") String userId) throws IOException { // Add userId as a param
        Material existingMaterial = materialService.getAllMaterials().stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Material not found"));

        if (!existingMaterial.getUploadedBy().equals(userId)) {
            throw new RuntimeException("You can only edit your own materials");
        }

        existingMaterial.setTitle(title);
        existingMaterial.setCategory(category);
        existingMaterial.setDate(date);
        existingMaterial.setTags(Arrays.asList(tags.split(",")));
        existingMaterial.setUrl(url != null ? url : "");
        existingMaterial.setDescription(description);

        if (pdf != null && !pdf.isEmpty()) {
            String oldPdfPath = existingMaterial.getPdfPath();
            if (oldPdfPath != null) {
                deleteFile(oldPdfPath);
            }
            String fileName = System.currentTimeMillis() + "_" + pdf.getOriginalFilename();
            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, pdf.getBytes());
            existingMaterial.setPdfPath("http://localhost:8080/" + UPLOAD_DIR + fileName);
        }

        return materialService.updateMaterial(id, existingMaterial);
    }

    @DeleteMapping("/{id}")
    public void deleteMaterial(@PathVariable Long id) {
        String pdfPath = materialService.deleteMaterial(id);
        if (pdfPath != null) {
            deleteFile(pdfPath);
        }
    }

    private void deleteFile(String fileUrl) {
        try {
            String filePath = fileUrl.replace("http://localhost:8080/", "");
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
        }
    }
}