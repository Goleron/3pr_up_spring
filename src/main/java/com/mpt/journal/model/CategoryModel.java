package com.mpt.journal.model;

public class CategoryModel {
    private int id;
    private String name;
    private boolean deleted = false;

    public CategoryModel() {}
    public CategoryModel(int id, String name) {
        this.id = id;
        this.name = name;
        this.deleted = false;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isDeleted() { return deleted; }
    public void setDeleted(boolean deleted) { this.deleted = deleted; }
}