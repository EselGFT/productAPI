package com.gfttraining.productAPI.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import com.gfttraining.productAPI.model.*;
import com.gfttraining.productAPI.repositories.CartRepository;
import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.CategoryRepository;
import com.gfttraining.productAPI.repositories.ProductRepository;


@Service
public class ProductService {

    private final CategoryRepository categoryRepository;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    public ProductService(CategoryRepository categoryRepository, ProductRepository productRepository, CartRepository cartRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;

    }

    public Product createProduct(ProductRequest productRequest) {

        Category category = categoryRepository.findById(productRequest.getCategory()).orElse(categoryRepository.findById("other").get());

        Product product = new Product(productRequest.getName(), productRequest.getDescription(), category, productRequest.getPrice(), productRequest.getStock(), productRequest.getWeight());

        return productRepository.save(product);

    }

    public Product updateProduct (long id, ProductRequest productRequest) throws NonExistingProductException, InvalidCartConnectionException {

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

            Product product = productRepository.save(productUpdate);
            sendModifiedDataToCart(product);
            return product;
        }
    }

    public Product sendModifiedDataToCart (Product product) throws InvalidCartConnectionException {
        ProductDTO productDTO = buildProductDTO(product);
        cartRepository.updateProduct(productDTO);
        return product;

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
                .orElseThrow(() -> new NonExistingProductException("Product IDs not found: " + id));
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
                .map(productRequest -> createProduct(productRequest))
                .toList();
    }

    public List<ProductDTO> createProductResponsesWithProductIDs(List<Long> ids) throws NonExistingProductException {
        List<Product> products = getProductsWithIDs(ids);
        return buildProductsDTOs(products);
    }

    public List<Product> getProductsWithIDs(List<Long> ids) throws NonExistingProductException {
        List<Product> foundIds = productRepository.findAllById(ids);
        if (foundIds.size() == ids.size()) {
            return foundIds;
        } else {
            List<Long> notFoundIds = ids.stream()
                    .filter(id -> foundIds.stream().noneMatch(product -> product.getId() == id))
                    .toList();

            throw new NonExistingProductException("Product IDs not found: " + notFoundIds);
        }
    }

    public int getNumberOfProducts() {
        return productRepository.findAll().size();
    }

    public ProductDTO buildProductDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setPrice(calculateDiscountedPrice(product));
        productDTO.setStock(product.getStock());;
        productDTO.setWeight(product.getWeight());
        return productDTO;
    }

    public List<ProductDTO> buildProductsDTOs(List<Product> products) {
        return products.stream()
                .map(this::buildProductDTO)
                .toList();
    }

    public BigDecimal calculateDiscountedPrice(Product product) {
        double priceNotRounded = (1 - product.getCategory().getDiscount() / 100) * product.getPrice();
        BigDecimal bd = new BigDecimal(priceNotRounded);
        BigDecimal roundedPrice = bd.setScale(2, RoundingMode.CEILING);
        return roundedPrice;
    }

    public List<ProductDTO> checkIfProductsCanBeSubmittedAndSubmit(List<ProductToSubmit> productsToSubmit) throws NonExistingProductException, NotEnoughStockException {
        List<Product> productsFound = getProductsWithProductsToSubmitIDs(productsToSubmit);
        List<Product> productsAvailable = getProductsWithEnoughStock(productsFound, productsToSubmit);
        List<Product> productsWithModifiedStock = subtractStockWithProductToSubmit(productsAvailable, productsToSubmit);
        return buildProductsDTOs(productsWithModifiedStock);
     }

    public List<Product> subtractStockWithProductToSubmit(List<Product> productsAvailable, List<ProductToSubmit> productsToSubmit) {
        return productsAvailable.stream().map(product -> subtractStock(product,productsToSubmit)).toList();

    }

    public Product subtractStock(Product product, List<ProductToSubmit> productsToSubmit) {
        for (ProductToSubmit productToSubmit : productsToSubmit) {
            if (product.getId() == productToSubmit.getId()) {
                product.setStock(product.getStock() - productToSubmit.getStock());
                break;
            }
        }
        return productRepository.save(product);
    }


    public List<Product> getProductsWithProductsToSubmitIDs(List<ProductToSubmit> productsToSubmit) throws NonExistingProductException {
        return getProductsWithIDs(productsToSubmit.stream()
                .map(ProductToSubmit::getId)
                .toList());
    }

    public List<Product> getProductsWithEnoughStock(List<Product> products, List<ProductToSubmit> productsToSubmit) throws NotEnoughStockException {
        List<Product> productsAvailable = products.stream()
                .filter(product -> isStockEnough(product, productsToSubmit))
                .toList();

        if (productsAvailable.size() == products.size()) {
            return products;
        } else {
            List<Long> notEnoughtStockIDs = products.stream()
                    .filter(product -> !productsAvailable.contains(product))
                    .map(Product::getId)
                    .toList();

            throw new NotEnoughStockException("Product IDs without required stock: " + notEnoughtStockIDs);
        }
    }

    public boolean isStockEnough(Product product, List<ProductToSubmit> productsToSubmit) {
        for (ProductToSubmit productToSubmit : productsToSubmit) {
            if (product.getId()==productToSubmit.getId() && product.getStock() >= productToSubmit.getStock()  ) {
                return true;
            }
        }
        return false;
    }
}
