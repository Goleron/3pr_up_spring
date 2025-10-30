//package com.mpt.journal.service;
//
//import com.mpt.journal.model.CategoryModel;
//import com.mpt.journal.repository.InMemoryCategoryRepository;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//public class InMemoryCategoryServiceImpl implements CategoryService {
//
//    private final InMemoryCategoryRepository repo;
//
//    public InMemoryCategoryServiceImpl(InMemoryCategoryRepository repo) {
//        this.repo = repo;
//    }
//
//    @Override
//    public List<CategoryModel> findAll(boolean includeDeleted) {
//        return repo.findAll(includeDeleted);
//    }
//
//    @Override
//    public CategoryModel add(CategoryModel category) {
//        return repo.save(category);
//    }
//
//    @Override
//    public CategoryModel ensureCategoryExists(String categoryName) {
//        if (categoryName == null || categoryName.isBlank()) {
//            return null;
//        }
//        List<CategoryModel> existing = repo.findAll(true);
//        Optional<CategoryModel> found = existing.stream()
//                .filter(c -> c.getName().equalsIgnoreCase(categoryName))
//                .findFirst();
//        if (found.isPresent()) {
//            return found.get();
//        }
//        CategoryModel newCategory = new CategoryModel(0, categoryName);
//        return repo.save(newCategory);
//    }
//
//    @Override
//    public void delete(int id, boolean hard) {
//        if (hard) {
//            repo.deleteByIdPhysical(id);
//        } else {
//            repo.deleteByIdLogical(id);
//        }
//    }
//
//    @Override
//    public void deleteMany(List<Integer> ids, boolean hard) {
//        repo.deleteMany(ids, hard);
//    }
//
//    @Override
//    public List<CategoryModel> search(String query, boolean includeDeleted) {
//        return repo.search(query, includeDeleted);
//    }
//
//    @Override
//    public List<CategoryModel> filter(String name, Integer minId, Integer maxId, boolean includeDeleted) {
//        return repo.filter(name, minId, maxId, includeDeleted);
//    }
//
//    @Override
//    public List<CategoryModel> getPage(List<CategoryModel> source, int page, int size) {
//        return repo.page(source, page, size);
//    }
//}