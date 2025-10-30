//package com.mpt.journal.service;
//
//import com.mpt.journal.entity.ProductEntity;
//import com.mpt.journal.model.OrderModel;
//import com.mpt.journal.repository.InMemoryOrderRepository;
//import com.mpt.journal.repository.InMemoryProductRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//public class InMemoryOrderServiceImpl implements OrderService {
//
//    private final InMemoryOrderRepository repo;
//    private final InMemoryProductRepository productRepo;
//
//    @Autowired
//    public InMemoryOrderServiceImpl(InMemoryOrderRepository repo, InMemoryProductRepository productRepo) {
//        this.repo = repo;
//        this.productRepo = productRepo;
//        this.repo.initSampleData();
//    }
//
//    @Override
//    public List<OrderModel> findAll(boolean includeDeleted) {
//        return repo.findAll(includeDeleted);
//    }
//
//    @Override
//    public OrderModel add(OrderModel order) {
//        return repo.save(order);
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
//    public List<OrderModel> search(String query, boolean includeDeleted) {
//        return repo.search(query, includeDeleted);
//    }
//
//    @Override
//    public List<OrderModel> filter(String customerName, Double minTotal, Double maxTotal, Integer minProducts, boolean includeDeleted) {
//        return repo.filter(customerName, minTotal, maxTotal, minProducts, includeDeleted);
//    }
//
//    @Override
//    public List<OrderModel> getPage(List<OrderModel> source, int page, int size) {
//        return repo.page(source, page, size);
//    }
//}