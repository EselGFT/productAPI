package com.gfttraining.productAPI.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.NonExistingProductException;
import com.gfttraining.productAPI.exceptions.NotEnoughStockException;
import com.gfttraining.productAPI.model.*;
import com.gfttraining.productAPI.repositories.CartRepository;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.gfttraining.productAPI.model.Category;
import com.gfttraining.productAPI.model.Product;
import com.gfttraining.productAPI.model.ProductRequest;
import com.gfttraining.productAPI.repositories.ProductRepository;


@Service
public class ProductService {

    private static Logger logger = Logger.getLogger(ProductService.class);
    private final CategoryService categoryService;

    private final ProductRepository productRepository;

    private final CartRepository cartRepository;

    public ProductService(CategoryService categoryService, ProductRepository productRepository, CartRepository cartRepository) {

        this.categoryService = categoryService;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;

    }

    public Product createProduct(ProductRequest productRequest) {

        Category category = categoryService.getCategoryByName(productRequest.getCategory());

        Product product = new Product(productRequest, category);

        return productRepository.save(product);

    }

    public Product updateProduct (long id, ProductRequest productRequest) throws NonExistingProductException, InvalidCartConnectionException {

        if (! productRepository.existsById(id)){
            logger.error("updateProduct error message: id not found, NonExistingProductException was thrown ");
            throw new NonExistingProductException("The provided ID is non existent");

        }

        Category category = categoryService.getCategoryByName(productRequest.getCategory());

        Product productToUpdate = productRepository.findById(id).get();
            productToUpdate.setName(productRequest.getName());
            productToUpdate.setDescription(productRequest.getDescription());
            productToUpdate.setCategory(category);
            productToUpdate.setPrice(productRequest.getPrice());
            productToUpdate.setStock(productRequest.getStock());
            productToUpdate.setWeight(productRequest.getWeight());

            sendModifiedDataToCart(productToUpdate);

        logger.info("update info message: product updated ");
            return productRepository.save(productToUpdate);

    }

    public Product sendModifiedDataToCart (Product product) throws InvalidCartConnectionException {
        ProductDTO productDTO = buildProductDTO(product);
        cartRepository.updateProduct(productDTO);
        return product;

    }

    public void deleteProduct (long id) throws NonExistingProductException {

        if (! productRepository.existsById(id)){
            logger.error("deleteProduct error message: id not found, NonExistingProductException was thrown ");
           throw new NonExistingProductException("The provided ID is non existent");
        }

        productRepository.deleteById(id);
        logger.info("deleteProduct info message: product deleted ");
    }

    public List<Product> listProducts() {
        logger.info("ListProducts info message: product listados ");
        return productRepository.findAll();

    }

    public List<Product> listProductsByNameContainsIgnoreCase(String name) {
        logger.info("product listados info message: by name ignore case");
        return productRepository.findByNameIgnoreCaseContaining(name);
    }

    public Product listProductById(long id) throws NonExistingProductException {
        return productRepository.findById(id)
                .orElseThrow(() -> new NonExistingProductException("Product IDs not found: " + id));
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        return productRequests.stream()
            .map(this::createProduct)
            .toList();
    }

    public List<ProductDTO> createProductResponsesWithProductIDs(List<Long> ids) throws NonExistingProductException {
        List<Product> products = getProductsByIDs(ids);
        return buildProductsDTOs(products);
    }

    public List<Product> getProductsByIDs(List<Long> ids) throws NonExistingProductException {
        List<Product> foundIds = productRepository.findAllById(ids);

        if (foundIds.size() == ids.size()) {
            return foundIds;
        }

        List<Long> notFoundIds = ids.stream()
            .filter(id -> foundIds.stream().noneMatch(product -> product.getId() == id))
            .toList();

        throw new NonExistingProductException("Product IDs not found: " + notFoundIds);
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
        double discountedPrice = (1 - product.getCategory().getDiscount() / 100) * product.getPrice();

        return BigDecimal.valueOf(discountedPrice).setScale(2, RoundingMode.CEILING); // Discounted price rounded to 2 decimal digits
    }

    public List<ProductDTO> checkIfEnoughStockAndSubtract(List<ProductToSubmit> productsToSubmit) throws NonExistingProductException, NotEnoughStockException {
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
        return getProductsByIDs(productsToSubmit.stream()
                .map(ProductToSubmit::getId)
                .toList());
    }

    public List<Product> getProductsWithEnoughStock(List<Product> products, List<ProductToSubmit> productsToSubmit) throws NotEnoughStockException {
        List<Product> productsAvailable = products.stream()
                .filter(product -> isStockEnough(product, productsToSubmit))
                .toList();

        if (productsAvailable.size() == products.size()) {
            return products;
        }

        List<Long> notEnoughStockIDs = products.stream()
            .filter(product -> ! productsAvailable.contains(product))
            .map(Product::getId)
            .toList();

        throw new NotEnoughStockException("Product IDs without required stock: " + notEnoughStockIDs);

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
