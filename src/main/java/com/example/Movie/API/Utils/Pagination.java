package com.example.Movie.API.Utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;

import java.util.Arrays;

@Getter
@Setter
public class Pagination implements Pageable {
    private int page;
    private int limit;
    private String[] sortBy;
    private String order;
    private String keyword; // Thêm keyword để tìm kiếm theo tên

    public Pagination() {
        this.page = 0;
        this.limit = 10;
        this.order = "asc";
        this.sortBy = new String[]{"id"}; // Mặc định sắp xếp theo id
    }

    public Pagination(int page, int limit, String[] sortBy, String order, String keyword) {
        this.page = Math.max(page, 0);
        this.limit = limit > 0 ? limit : 10;
        this.sortBy = (sortBy != null && sortBy.length > 0) ? sortBy : new String[]{"id"};
        this.order = (order != null && (order.equalsIgnoreCase("asc") || order.equalsIgnoreCase("desc"))) ? order : "asc";
        this.keyword = keyword;
    }

    @Override
    public int getPageNumber() {
        return this.page;
    }

    @Override
    public int getPageSize() {
        return this.limit;
    }

    @Override
    public long getOffset() {
        return (long) this.page * this.limit;
    }

    @Override
    public Sort getSort() {
        return Sort.by(Arrays.stream(sortBy)
                .map(field -> new Sort.Order(Sort.Direction.fromString(order), field))
                .toList());
    }

    @Override
    public Pageable next() {
        return PageRequest.of(this.page + 1, this.limit, this.getSort());
    }

    @Override
    public Pageable previousOrFirst() {
        return PageRequest.of(Math.max(this.page - 1, 0), this.limit, this.getSort());
    }

    @Override
    public Pageable first() {
        return PageRequest.of(0, this.limit, this.getSort());
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return PageRequest.of(pageNumber, this.limit, this.getSort());
    }

    @Override
    public boolean hasPrevious() {
        return this.page > 0;
    }
}
