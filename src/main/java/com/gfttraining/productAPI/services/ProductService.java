package com.gfttraining.productAPI.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import com.gfttraining.productAPI.ProductApiApplication;
import com.gfttraining.productAPI.exceptions.InvalidCartConnectionException;
import com.gfttraining.productAPI.exceptions.InvalidCartResponseException;
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

        logger.info(" ProductService's CreateProducts starts ");
        Category category = categoryService.getCategoryByName(productRequest.getCategory());

        Product product = new Product(productRequest, category);

        return productRepository.save(product);

    }

    public Product updateProduct (long id, ProductRequest productRequest) throws NonExistingProductException, InvalidCartConnectionException, InvalidCartResponseException {

        if (! productRepository.existsById(id)){
            logger.error(" ProductService's CreateProducts started but throws NonExistingProductException  ");
            throw new NonExistingProductException("The provided ID is non existent");

        }
        logger.info(" ProductService's UpdateProducts starts ");
        Category category = categoryService.getCategoryByName(productRequest.getCategory());

        Product productToUpdate = productRepository.findById(id).get();
            productToUpdate.setName(productRequest.getName());
            productToUpdate.setDescription(productRequest.getDescription());
            productToUpdate.setCategory(category);
            productToUpdate.setPrice(productRequest.getPrice());
            productToUpdate.setStock(productRequest.getStock());
            productToUpdate.setWeight(productRequest.getWeight());

            sendModifiedDataToCart(productToUpdate);
            logger.info(" ProductService's UpdateProducts started and sendModifiedDataToCart was executed  ");

            return productRepository.save(productToUpdate);

    }

    public Product sendModifiedDataToCart (Product product) throws InvalidCartConnectionException, InvalidCartResponseException {
        logger.info(" ProductService's sendModifiedDataToCart starts ");
        ProductDTO productDTO = buildProductDTO(product);
        cartRepository.updateProduct(productDTO);
        logger.info(" ProductService's sendModifiedDataToCart started and cart repository's updateProduct was executed ");
        return product;

    }

    public void deleteProduct (long id) throws NonExistingProductException {

        if (! productRepository.existsById(id)){
           logger.error(" ProductService's deleteProduct started but throws NonExistingProductException  ");
           throw new NonExistingProductException("The provided ID is non existent");
        }
        logger.info(" ProductService's deleteProduct starts ");
        productRepository.deleteById(id);
        logger.info("ProductService's deleteProduct: product deleted ");
    }

    public List<Product> listProducts() {
        logger.info(" ProductService's listProducts starts ");
        return productRepository.findAll();
    }

    public List<Product> listProductsByNameContainsIgnoreCase(String name) {
        logger.info(" ProductService's listProductsByNameContainsIgnoreCase starts ");
        return productRepository.findByNameIgnoreCaseContaining(name);
    }

    public Product listProductById(long id) throws NonExistingProductException {
        logger.info(" ProductService's listProductsByNameContainsIgnoreCase starts ");
        return productRepository.findById(id)
                .orElseThrow(() -> {
                    logger.error(" ProductService's listProductById started but throws NonExistingProductException ");
                    return new NonExistingProductException("Product IDs not found: " + id);});
    }

    public List<Product> createProducts(List<ProductRequest> productRequests) {
        logger.info(" ProductService's createProducts starts ");
        return productRequests.stream()
            .map(this::createProduct)
            .toList();
    }

    public List<ProductDTO> createProductResponsesWithProductIDs(List<Long> ids) throws NonExistingProductException {
        logger.info(" ProductService's createProductResponsesWithProductIDs starts ");
        List<Product> products = getProductsByIDs(ids);
        return buildProductsDTOs(products);
    }

    public List<Product> getProductsByIDs(List<Long> ids) throws NonExistingProductException {
        logger.info(" ProductService's createProductResponsesWithProductIDs starts ");
        List<Product> foundIds = productRepository.findAllById(ids);

        if (foundIds.size() == ids.size()) {
            return foundIds;
        }

        List<Long> notFoundIds = ids.stream()
            .filter(id -> foundIds.stream().noneMatch(product -> product.getId() == id))
            .toList();
        logger.warn(" ProductService's createProductResponsesWithProductIDs started and throws NonExistingProductException ");
        throw new NonExistingProductException("Product IDs not found: " + notFoundIds);
    }

    public int getNumberOfProducts() {
        return productRepository.findAll().size();
    }

    public ProductDTO buildProductDTO(Product product) {
        logger.info(" ProductService's buildProductDTO starts ");
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setPrice(calculateDiscountedPrice(product));
        productDTO.setStock(product.getStock());;
        productDTO.setWeight(product.getWeight());
        return productDTO;
    }

    public List<ProductDTO> buildProductsDTOs(List<Product> products) {
        logger.info(" ProductService's buildProductsDTOs starts ");
        return products.stream()
                .map(this::buildProductDTO)
                .toList();
    }

    public BigDecimal calculateDiscountedPrice(Product product) {
        logger.info(" ProductService's calculateDiscountedPrice starts ");
        double discountedPrice = (1 - product.getCategory().getDiscount() / 100) * product.getPrice();

        return BigDecimal.valueOf(discountedPrice).setScale(2, RoundingMode.CEILING); // Discounted price rounded to 2 decimal digits
    }

    public List<ProductDTO> checkIfEnoughStockAndSubtract(List<ProductToSubmit> productsToSubmit) throws NonExistingProductException, NotEnoughStockException {
        logger.info(" ProductService's checkIfEnoughStockAndSubtract starts ");
        List<Product> productsFound = getProductsWithProductsToSubmitIDs(productsToSubmit);
        logger.info(" ProductService checkIfEnoughStockAndSubtract's getProductsWithProductsToSubmitIDs was executed ");
        List<Product> productsAvailable = getProductsWithEnoughStock(productsFound, productsToSubmit);
        logger.info(" ProductService checkIfEnoughStockAndSubtract's getProductsWithEnoughStock was executed ");
        List<Product> productsWithModifiedStock = subtractStockWithProductToSubmit(productsAvailable, productsToSubmit);
        logger.info(" ProductService checkIfEnoughStockAndSubtract's subtractStockWithProductToSubmit was executed, so it build a ProductDTO");
        return buildProductsDTOs(productsWithModifiedStock);
     }

    public List<Product> subtractStockWithProductToSubmit(List<Product> productsAvailable, List<ProductToSubmit> productsToSubmit) {
        logger.info(" ProductService's subtractStockWithProductToSubmit starts ");
        return productsAvailable.stream().map(product -> subtractStock(product,productsToSubmit)).toList();
    }

    public Product subtractStock(Product product, List<ProductToSubmit> productsToSubmit) {
        logger.info(" ProductService's subtractStock starts ");
        for (ProductToSubmit productToSubmit : productsToSubmit) {
            if (product.getId() == productToSubmit.getId()) {
                product.setStock(product.getStock() - productToSubmit.getStock());
                logger.info(" ProductService's subtractStock started and a product stock was modified ");

            }
        }

        return productRepository.save(product);
    }

    public List<Product> getProductsWithProductsToSubmitIDs(List<ProductToSubmit> productsToSubmit) throws NonExistingProductException {
        logger.info(" ProductService's getProductsWithProductsToSubmitIDs starts ");
        return getProductsByIDs(productsToSubmit.stream()
                .map(ProductToSubmit::getId)
                .toList());
    }

    public List<Product> getProductsWithEnoughStock(List<Product> products, List<ProductToSubmit> productsToSubmit) throws NotEnoughStockException {
        logger.info(" ProductService's getProductsWithEnoughStock starts ");
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
        logger.warn(" ProductService's getProductsWithEnoughStock started and throws NotEnoughStockException ");
        throw new NotEnoughStockException("Product IDs without required stock: " + notEnoughStockIDs);

    }

    public boolean isStockEnough(Product product, List<ProductToSubmit> productsToSubmit) {
        logger.info(" ProductService's isStockEnough starts ");
        for (ProductToSubmit productToSubmit : productsToSubmit) {
            if (product.getId()==productToSubmit.getId() && product.getStock() >= productToSubmit.getStock()  ) {
                logger.info(" ProductService's isStockEnough returns true");
                return true;
            }
        }
        return false;
    }
}
