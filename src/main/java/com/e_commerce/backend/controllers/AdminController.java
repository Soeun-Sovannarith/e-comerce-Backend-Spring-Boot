package com.e_commerce.backend.controllers;

import com.e_commerce.backend.models.Product;
import com.e_commerce.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private ProductService productService;

    @PostMapping("/products")
    public ResponseEntity<?> createProduct(
            @RequestPart("product") Product product,
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            Product savedProduct = productService.addProduct(product, imageFile);
            return ResponseEntity.ok(savedProduct);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to create product: " + e.getMessage());
        }
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<?> updateProduct(
            @PathVariable int id,
            @RequestPart("product") Product product,
            @RequestPart("imageFile") MultipartFile imageFile) {
        try {
            Product updatedProduct = productService.updateProductbyID(id, product, imageFile);
            if (updatedProduct == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(updatedProduct);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Failed to update product: " + e.getMessage());
        }
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteproduct(id);
            return ResponseEntity.ok("Product deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to delete product: " + e.getMessage());
        }
    }

    @GetMapping("/products/all")
    public ResponseEntity<List<Product>> getAllProductsForAdmin() {
        List<Product> products = productService.getAllofProducts();
        return ResponseEntity.ok(products);
    }
}
