//package com.gym.services;
//
//import com.gym.dto.ImageDTO;
//import com.gym.dto.ProductDTO;
//import com.gym.entities.Category;
//import com.gym.entities.Image;
//import com.gym.entities.Product;
//import com.gym.exceptions.ResourceNotFoundException;
//import com.gym.repositories.CategoryRepository;
//import com.gym.repositories.ImageRepository;
//import com.gym.repositories.ProductRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.*;
//import java.util.stream.Collectors;
//
//@Service
//public class ProductService {
//
//    private ProductRepository productRepository;
//
//    private CategoryRepository categoryRepository;
//
//    private ImageRepository imageRepository;
//
//    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ImageRepository imageRepository) {
//        this.productRepository = productRepository;
//        this.categoryRepository = categoryRepository;
//        this.imageRepository = imageRepository;
//    }
//
//    public List<ProductDTO> getAllProduct(){
//        List<Product> productList = (List<Product>) productRepository.findAll();
//        List<ProductDTO> productsDTOList;
//        productsDTOList= productList.stream().map(p -> p.toDto()).collect(Collectors.toList());
//
//        return productsDTOList;
//    }
//
//    public ProductDTO saveProduct(ProductDTO productDTO) throws ResourceNotFoundException {
////        Optional<Category> category = categoryRepository.findById(productDTO.getCategory().getId());
////        if (category.isPresent()) {
////
////            //crear el producto y obtener id
////            Product product = productRepository.save(productDTO.toEntity());
////            Long productId = product.getId();
////
////            //obtener el set de imagenes y crear otro set vacio
////            Set<Image> images = productDTO.getImages();
////            Set<Image> imagesAdded = new HashSet<>();
////
////            //por cada imagen setear el producto, guardarlo y setearlo en en nuevo set
////            for (Image i : images) {
////
////                Product product2 = new Product(productId);
////                i.setProduct(product2);
////
////                Image imageSave = imageRepository.save(i);
////                imagesAdded.add(imageSave);
////            }
////
////
////            //setear el nuevo set en el producto original y guardar
////            product.setImages(imagesAdded);
////            productRepository.save(product);
////
////            return  product.toDto();
////
////        } else {
////            if (category.isEmpty()) {
////                throw new ResourceNotFoundException("The category with id " + productDTO.getCategory().getId() + " has not been found.");
////            } else {
////                throw new ResourceNotFoundException("The category with id " + productDTO.getCategory().getId() + "and the city with id" + productDTO.getCategory().getId() + " has not been found.");
////            }
////        }
//        // Verificar si la categoría existe
//        Optional<Category> categoryOptional = categoryRepository.findById(productDTO.getCategory().getId());
//        if (categoryOptional.isEmpty()) {
//            throw new ResourceNotFoundException("Category with ID " + productDTO.getCategory().getId() + " not found");
//        }
//        Category category = categoryOptional.get();
//
//        Set<Image> images = productDTO.getImages();
//        if (images == null || images.isEmpty()) {
//            throw new IllegalArgumentException("Product must have at least one image");
//        }
//
//        // Crear el producto
//        Product product = new Product();
//        product.setName(productDTO.getName());
//        product.setPrice(productDTO.getPrice());
//        product.setDescription(productDTO.getDescription());
//        product.setStock(productDTO.getStock());
//        product.setCategory(category);
//
//        // Guardar el producto
//        Product savedProduct = productRepository.save(product);
//
//        // Guardar las imágenes asociadas al producto
////        Set<Image> images = productDTO.getImages();
//        for (Image image : images) {
//            image.setProduct(savedProduct);
//        }
//        savedProduct.setImages(images); // Setear el conjunto de imágenes en el producto
//        imageRepository.saveAll(images);
//
//        // Retornar el DTO del producto guardado
//        return savedProduct.toDto();
//    }
//
//    public ProductDTO getProduct(Long id) throws ResourceNotFoundException {
//        Optional<Product> product = productRepository.findById(id);
//
//        if (product.isPresent()){
//            return product.get().toDto();
//        }else {
//            throw new ResourceNotFoundException("The product with id " + id + " has not been found to be deleted.");
//        }
//
//    }
//
//    public List<ProductDTO> getProductsByCategory(Long category_id) throws ResourceNotFoundException {
//
//        List<Product> productList = null;
//        List<ProductDTO> productsDTOList;
//
//        Optional<Category> searchedCategory = categoryRepository.findById(category_id);
//        if (searchedCategory.isPresent()){
//            productList =productRepository.getProductsByCategory(category_id);
//        }else {
//            throw new ResourceNotFoundException("The category with id " + category_id + " has not been found.");
//        }
//
//        productsDTOList= productList.stream().map(p -> p.toDto()).collect(Collectors.toList());
//        return productsDTOList ;
//
//    }
//
//    public ProductDTO updateProduct(ProductDTO productDTO) throws ResourceNotFoundException {
//        ProductDTO productDTO1 = getProduct(productDTO.getId());
//        Product product = new Product();
//        if (productDTO1!=null) {
//            product = productRepository.save(productDTO.toEntity());
//            return product.toDto();
//        } else {
//            throw new ResourceNotFoundException("The product with id " + productDTO1.getId() + " has not been found.");
//        }
//    }
//
//    public String deleteProduct(Long id) throws ResourceNotFoundException{
//        Optional<Product> productToDelete = productRepository.findById(id);
//
//        if (productToDelete.isPresent()) {
//            productRepository.delete(productToDelete.get());
//            return "The product with id "+productToDelete.get().getId()+ " has been deleted ";
//
//        } else {
//            throw new ResourceNotFoundException("The product with id " + id + " has not been found to be deleted.");
//        }
//
//    }
//
//}
