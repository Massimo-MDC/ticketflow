package it.ticketflow.ticketflow.dto.category;

import it.ticketflow.ticketflow.entity.Category;

public record CategoryResponseDTO(
        Long id,
        String name,
        String description,
        String iconUrl
) {
    public static CategoryResponseDTO fromEntity(Category category) {
        return new CategoryResponseDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getIconUrl()
        );
    }
}