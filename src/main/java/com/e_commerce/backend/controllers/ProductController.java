package com.e_commerce.backend.controllers;


import com.e_commerce.backend.models.Product;
import com.e_commerce.backend.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/product")
    public ResponseEntity<List<Product>> getAllProudct(){
        return new ResponseEntity<>(productService.getAllofProducts(), HttpStatus.OK);
    }

    @GetMapping("product/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable int id) {
        try {
            Product product = productService.getProductById(id);

            if (product != null) {
                return ResponseEntity.ok(product); // 200 OK with product
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // 404 Not Found
            }
        } catch (Exception e) {
            // Optional: log the error
            System.err.println("Error fetching product with id " + id + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // return 404 for safety
        }
    }

    @PutMapping("/product/update/{id}")
    public ResponseEntity<String> editProduct(@PathVariable int id, @RequestPart Product product, @RequestPart(required = false) MultipartFile productImage) {

        Product product1 = null;
        try {
            product1 = productService.updateProductbyID(id, product, productImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (product1 != null) {
            return new ResponseEntity<>("Successfully updated",HttpStatus.OK);
        }
        else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

    }




    @PostMapping("/product/add")
    public ResponseEntity<?> addProduct(@RequestPart Product product, @RequestPart MultipartFile productImage) {
        try{
            Product product1 = productService.addProduct(product,productImage);
            return new ResponseEntity<>(product1,HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }


    }

    @GetMapping("/product/{id}/image")
    public ResponseEntity<byte[]> getProductImage(@PathVariable int id) {
        Product product = productService.getProductById(id);
        if (product != null && product.getImageData() != null) {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + product.getImageName() + "\"")
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .header(HttpHeaders.EXPIRES, "0")
                    .contentType(MediaType.parseMediaType(product.getImageType()))
                    .body(product.getImageData());
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable int id) {

        Product product = productService.getProductById(id);
        if (product != null) {
            productService.deleteproduct(id);
            return new ResponseEntity<>("Successfully deleted",HttpStatus.OK);
        }
        else  {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }


    }

    @GetMapping("product/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String keyword){
        List<Product> products = productService.searchProducts(keyword); 
        return new ResponseEntity<>(products, HttpStatus.OK);

    }


}
