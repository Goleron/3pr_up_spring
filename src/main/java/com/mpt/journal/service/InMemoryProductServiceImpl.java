//package com.mpt.journal.service;
//
//import com.mpt.journal.entity.ProductEntity;
//import com.mpt.journal.model.ProductModel;
//import com.mpt.journal.repository.InMemoryProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class InMemoryProductServiceImpl implements ProductService {
//
//    private final InMemoryProductRepository repository;
//    private final CategoryService categoryService;
//
//    @Autowired
//    public InMemoryProductServiceImpl(InMemoryProductRepository repository, CategoryService categoryService) {
//        this.repository = repository;
//        this.categoryService = categoryService;
//        this.repository.initSampleData();
//    }
//
//    private ProductModel toModel(ProductEntity e) {
//        ProductModel m = new ProductModel(e.getId(), e.getName(), e.getCategory(), e.getPrice(), e.getQuantity(), e.getBrand());
//        m.setDeleted(e.isDeleted());
//        return m;
//    }
//
//    @Override
//    public List<ProductModel> findAllProducts(boolean includeDeleted) {
//        return repository.findAll(includeDeleted).stream().map(this::toModel).collect(Collectors.toList());
//    }
//
//    @Override
//    public ProductModel addProduct(ProductModel product) {
//        categoryService.ensureCategoryExists(product.getCategory());
//        ProductEntity ent = new ProductEntity(0, product.getName(), product.getCategory(), product.getPrice(), product.getQuantity(), product.getBrand());
//        ProductEntity saved = repository.save(ent);
//        return toModel(saved);
//    }
//
//    @Override
//    public ProductModel updateProduct(ProductModel product) {
//        categoryService.ensureCategoryExists(product.getCategory());
//        ProductEntity ent = new ProductEntity(product.getId(), product.getName(), product.getCategory(), product.getPrice(), product.getQuantity(), product.getBrand());
//        ent.setDeleted(product.isDeleted());
//        ProductEntity saved = repository.save(ent);
//        return toModel(saved);
//    }
//
//    @Override
//    public void deleteProduct(int id, boolean hard) {
//        if (hard) {
//            repository.deleteByIdPhysical(id);
//        } else {
//            repository.deleteByIdLogical(id);
//        }
//    }
//
//    @Override
//    public void deleteMany(List<Integer> ids, boolean hard) {
//        repository.deleteMany(ids, hard);
//    }
//
//    @Override
//    public List<ProductModel> searchProducts(String query, boolean includeDeleted) {
//        return repository.search(query, includeDeleted).stream().map(this::toModel).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ProductModel> filterProducts(String category, String brand, Double minPrice, Double maxPrice, boolean includeDeleted) {
//        return repository.filter(category, brand, minPrice, maxPrice, includeDeleted).stream().map(this::toModel).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<ProductModel> getProductsPage(List<ProductModel> source, int page, int size) {
//        if (size < 10) size = 10;
//        if (page < 1) page = 1;
//        int from = (page - 1) * size;
//        if (from >= source.size()) return List.of();
//        return source.stream().skip(from).limit(size).collect(Collectors.toList());
//    }
//}