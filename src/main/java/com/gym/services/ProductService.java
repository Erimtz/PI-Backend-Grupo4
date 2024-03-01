package com.gym.services;

import com.gym.dto.ProductDTO;
import com.gym.entities.Category;
import com.gym.entities.Product;
import com.gym.exceptions.BadRequestException;
import com.gym.exceptions.ResourceNotFoundException;
import com.gym.repositories.CategoryRepository;
import com.gym.repositories.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private ProductRepository productRepository;

    private CategoryRepository categoryRepository;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<ProductDTO> getAllProduct(){
        List<Product> productList = (List<Product>) productRepository.findAll();
        List<ProductDTO> productsDTOList;
        productsDTOList= productList.stream().map(p -> p.toDto()).collect(Collectors.toList());

        return productsDTOList;
    }

    public List<ProductDTO> getProductsByName(String name) {
        List<Product> productList = productRepository.findByName(name);
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }

    public List<ProductDTO> getProductsByPriceRange(Double minPrice, Double maxPrice) {
        List<Product> productList = productRepository.findByPriceBetween(minPrice, maxPrice);
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProductSortedByPriceAsc() {
        List<Product> productList = (List<Product>) productRepository.findAll();
        productList.sort(Comparator.comparing(Product::getPrice));
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }

    public List<ProductDTO> getAllProductSortedByPriceDesc() {
        List<Product> productList = (List<Product>) productRepository.findAll();
        productList.sort(Comparator.comparing(Product::getPrice).reversed());
        return productList.stream().map(Product::toDto).collect(Collectors.toList());
    }

    public ProductDTO saveProduct(ProductDTO productDTO) throws ResourceNotFoundException, BadRequestException {
        // Verificar si el precio es nulo
        if (productDTO.getPrice() == null) {
            throw new BadRequestException("El precio del producto es requerido.");
        }

        Optional<Category> category = categoryRepository.findById(productDTO.getCategory().getId());
        if (category.isPresent()) {
            Product productEntity = productDTO.toEntity();
            productEntity.setCategory(category.get());
            // Crear el producto y obtener id
            Product product = productRepository.save(productEntity);

            // Devolver el DTO del producto guardado
            return product.toDto();
        } else {
            if (category.isEmpty()) {
                throw new ResourceNotFoundException("La categoría con el ID " + productDTO.getCategory().getId() + " no se encontró.");
            } else {
                throw new ResourceNotFoundException("La categoría con el ID " + productDTO.getCategory().getId() + " y la ciudad con el ID " + productDTO.getCategory().getId() + " no se encontraron.");
            }
        }
    }

    public ProductDTO addImagesToProduct(Long productId, List<String> imageNames) throws ResourceNotFoundException {
        // Obtener el producto por ID
        Optional<Product> optionalProduct = productRepository.findById(productId);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();

            // Agregar las imágenes al producto
            String currentImages = product.getImages();
            if (currentImages == null) {
                currentImages = "";
            }
            String newImages = imageNames.stream().collect(Collectors.joining(";"));
            String updatedImages = currentImages.isEmpty() ? newImages : currentImages + ";" + newImages;
            product.setImages(updatedImages);

            // Guardar el producto actualizado
            Product savedProduct = productRepository.save(product);
            return savedProduct.toDto();
        } else {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }
    }

    public ProductDTO getProduct(Long id) throws ResourceNotFoundException {
        Optional<Product> product = productRepository.findById(id);

        if (product.isPresent()){
            return product.get().toDto();
        }else {
            return null;
        }

    }

    public List<ProductDTO> getProductsByCategory(Long category_id) throws ResourceNotFoundException {

        List<Product> productList = null;
        List<ProductDTO> productsDTOList;

        Optional<Category> searchedCategory = categoryRepository.findById(category_id);
        if (searchedCategory.isPresent()){
            productList =productRepository.getProductsByCategory(category_id);
        }else {
            throw new ResourceNotFoundException("The category with id " + category_id + " has not been found.");
        }

        productsDTOList= productList.stream().map(p -> p.toDto()).collect(Collectors.toList());
        return productsDTOList ;

    }

    public ProductDTO updateProduct(ProductDTO productDTO) throws ResourceNotFoundException {
        ProductDTO productDTO1 = getProduct(productDTO.getId());
        Product product = new Product();
        if (productDTO1!=null) {
            product = productRepository.save(productDTO.toEntity());
            return product.toDto();
        } else {
            throw new ResourceNotFoundException("The product with id " + productDTO1.getId() + " has not been found.");
        }
    }

    public String deleteProduct(Long id) throws ResourceNotFoundException{
        Optional<Product> productToDelete = productRepository.findById(id);

        if (productToDelete.isPresent()) {
            productRepository.delete(productToDelete.get());
            return "The product with id "+productToDelete.get().getId()+ " has been deleted ";

        } else {
            throw new ResourceNotFoundException("The product with id " + id + " has not been found to be deleted.");
        }

    }

}
