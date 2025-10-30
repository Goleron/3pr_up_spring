package com.mpt.journal.entity;

import com.mpt.journal.model.ProductModel;


public class ProductEntity extends ProductModel {
    public ProductEntity() { super(); }

    public ProductEntity(int id, String name, String category, double price, int quantity, String brand) {
        super(id, name, category, price, quantity, brand);
    }
}
