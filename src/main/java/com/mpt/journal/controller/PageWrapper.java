package com.mpt.journal.controller;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class PageWrapper<T> {
    private final Page<T> page;
    private final String url;

    public PageWrapper(Page<T> page, String url) {
        this.page = page;
        this.url = url;
    }

    public List<T> getContent() { return page.getContent(); }
    public int getNumber() { return page.getNumber(); }
    public int getSize() { return page.getSize(); }
    public int getTotalPages() { return page.getTotalPages(); }
    public long getTotalElements() { return page.getTotalElements(); }
    public boolean isFirst() { return page.isFirst(); }
    public boolean isLast() { return page.isLast(); }
    public boolean hasNext() { return page.hasNext(); }
    public boolean hasPrevious() { return page.hasPrevious(); }

    public List<Integer> getPageNumbers() {
        List<Integer> pageNumbers = new ArrayList<>();
        int current = page.getNumber();
        int total = page.getTotalPages();
        int start = Math.max(0, current - 2);
        int end = Math.min(total, current + 3);

        for (int i = start; i < end; i++) {
            pageNumbers.add(i);
        }
        return pageNumbers;
    }

    public String pageUrl(int page) {
        return url + (url.contains("?") ? "&" : "?") + "page=" + page;
    }
}