package it.ticketflow.ticketflow.dto.graphql;

public record PageInfoDTO(
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize,
        boolean hasNext,
        boolean hasPrevious
) {
    public static PageInfoDTO from(org.springframework.data.domain.Page<?> page) {
        return new PageInfoDTO(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
        );
    }
}