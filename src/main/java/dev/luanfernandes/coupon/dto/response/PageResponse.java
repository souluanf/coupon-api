package dev.luanfernandes.coupon.dto.response;

import java.util.List;

public record PageResponse<T>(int pageNumber, int pageSize, long totalElements, boolean hasNext, List<T> content) {

    public static <T> PageResponse<T> of(int pageNumber, int pageSize, long totalElements, List<T> content) {
        boolean hasNext = (long) (pageNumber + 1) * pageSize < totalElements;
        return new PageResponse<>(pageNumber, pageSize, totalElements, hasNext, content);
    }
}
