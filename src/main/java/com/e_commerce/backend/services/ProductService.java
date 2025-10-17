package com.e_commerce.backend.services;

import com.e_commerce.backend.models.Product;
import com.e_commerce.backend.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getAllofProducts(){
        return productRepository.findAll();
    }

    public Product getProductById(int id){
        return productRepository.findById(id).get();
    }

    public Product addProduct(Product product, MultipartFile productImage) throws IOException {
        product.setImageName(productImage.getOriginalFilename());
        product.setImageType(productImage.getContentType());
        product.setImageData(productImage.getBytes());
       return productRepository.save(product);
    }

    public Product updateProductbyID(int id, Product product, MultipartFile productImage) throws IOException {
        // 1️⃣ Find existing product by ID
        Product existingProduct = productRepository.findById(id).orElse(null);
        if (existingProduct == null) {
            return null; // Return null to indicate product not found
        }

        // 2️⃣ Update fields
        existingProduct.setName(product.getName());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setCategory(product.getCategory());

        existingProduct.setImageData(productImage.getBytes());
        existingProduct.setImageName(productImage.getOriginalFilename());
        existingProduct.setImageType(productImage.getContentType());


        // 4️⃣ Save updated product
        return productRepository.save(existingProduct);
    }


    public void deleteproduct(int id){
        productRepository.deleteById(id);
    }

    public List<Product> searchProducts(String keyword) {
       return productRepository.searchProducts(keyword);
    }

    public Product updateProduct(Product product) {
        return null;
    }

    public String deleteProduct(int id) {
        return null;
    }
}
