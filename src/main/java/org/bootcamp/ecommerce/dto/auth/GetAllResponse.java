package org.bootcamp.ecommerce.dto.auth;

import lombok.Builder;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.List;

@Builder
public record GetAllResponse<T>(
        List<T> data,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean lastPage

) {
    public static <E> GetAllResponse<E> empty(Pageable pageable) {
        return new GetAllResponse<>(
                Collections.emptyList(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0,
                0,
                true
        );
    }
}
