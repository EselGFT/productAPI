package com.gfttraining.productAPI.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.model.ProductDTO;
import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.exceptions.NotAllProductsFoundException;
import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;


@Service
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;

    }

    public Product createProduct(ProductRequest productRequest) {

        Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());

        Product product = new Product(productRequest.getName(), productRequest.getDescription(), category, productRequest.getPrice(), productRequest.getStock(), productRequest.getWeight());

        return productRepository.save(product);

    }

    public Product updateProduct (long id, ProductRequest productRequest) throws NonExistingProductException {

    	Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());


        if (productRepository.findById(id).isEmpty()){
            throw new NonExistingProductException("The provided ID is non existent");
        }else {
            Product productUpdate = productRepository.findById(id).get();
            productUpdate.setName(productRequest.getName());
            productUpdate.setDescription(productRequest.getDescription());
            productUpdate.setCategory(category);
            productUpdate.setPrice(productRequest.getPrice());
            productUpdate.setStock(productRequest.getStock());
            productUpdate.setWeight(productRequest.getWeight());

            return productRepository.save(productUpdate);
        }
    }

    public void deleteProduct (long id) throws NonExistingProductException {

        if (productRepository.findById(id).isEmpty()){
           throw new NonExistingProductException("The provided ID is non existent");
         }else {
            productRepository.deleteById(id);
         }

    }

    public List<Product> listProducts() {
        return productRepository.findAll();
    }

    public List<Product> listProductsByNameContainsIgnoreCase(String name) {
        return productRepository.findByNameIgnoreCaseContaining(name);
    }

    public Product listProductById(long id) throws NonExistingProductException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NonExistingProductException("The provided ID is non existent"));
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(productRequest -> createProduct(productRequest))
                .toList();
    }

    public List<ProductDTO> createProductResponsesWithProductIDs(List<Long> ids) throws NotAllProductsFoundException {
        List<Product> products = getProductsWithIDs(ids);
        return buildProductsDTOs(products);
    }

    public List<Product> getProductsWithIDs(List<Long> ids) throws NotAllProductsFoundException {
        List<Product> foundIds = productRepository.findAllById(ids);
        if (foundIds.size() == ids.size()) {
            return foundIds;
        } else {
            List<Long> notFoundIds = ids.stream()
                    .filter(id -> foundIds.stream().noneMatch(product -> product.getId() == id))
                    .toList();

            throw new NotAllProductsFoundException("Product IDs not found: " + notFoundIds);
        }
    }

    public int getNumberOfProducts() {
        return productRepository.findAll().size();
    }
    public List<ProductDTO> buildProductsDTOs(List<Product> products) {
        return products.stream()
                .map(product -> {
                    ProductDTO productDTO = new ProductDTO();
                    productDTO.setId(product.getId());
                    productDTO.setPrice(calculateDiscountedPrice(product));
                    productDTO.setStock(product.getStock());;
                    productDTO.setWeight(product.getWeight());
                    return productDTO;
                })
                .toList();
    }

    public BigDecimal calculateDiscountedPrice(Product product) {
        double priceNotRounded = (1 - product.getCategory().getDiscount() / 100) * product.getPrice();
        BigDecimal bd = new BigDecimal(priceNotRounded);
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);
        return roundedPrice;
    }

}
