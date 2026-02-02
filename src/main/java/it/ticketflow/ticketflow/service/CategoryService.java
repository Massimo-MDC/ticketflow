package it.ticketflow.ticketflow.service;

import it.ticketflow.ticketflow.dto.category.CategoryRequestDTO;
import it.ticketflow.ticketflow.dto.category.CategoryResponseDTO;
import it.ticketflow.ticketflow.entity.Category;
import it.ticketflow.ticketflow.exception.BadRequestException;
import it.ticketflow.ticketflow.exception.NotFoundException;
import it.ticketflow.ticketflow.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;

    public List<CategoryResponseDTO> findAll() {
        return categoryRepository.findAll().stream()
                .map(CategoryResponseDTO::fromEntity)
                .toList();
    }

    public Page<CategoryResponseDTO> findAllPaged(Pageable pageable) {
        return categoryRepository.findAll(pageable)
                .map(CategoryResponseDTO::fromEntity);
    }

    public CategoryResponseDTO findById(Long id) {
        Category category = getCategoryById(id);
        return CategoryResponseDTO.fromEntity(category);
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category", id));
    }

    @Transactional
    public CategoryResponseDTO create(CategoryRequestDTO dto) {
        if (categoryRepository.existsByName(dto.name())) {
            throw new BadRequestException("Categoria con nome '" + dto.name() + "' già esistente");
        }

        Category category = Category.builder()
                .name(dto.name())
                .description(dto.description())
                .build();

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDTO.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO update(Long id, CategoryRequestDTO dto) {
        Category category = getCategoryById(id);

        // Verifica nome duplicato (escludendo la categoria corrente)
        categoryRepository.findByName(dto.name())
                .filter(c -> !c.getId().equals(id))
                .ifPresent(c -> {
                    throw new BadRequestException("Categoria con nome '" + dto.name() + "' già esistente");
                });

        category.setName(dto.name());
        category.setDescription(dto.description());

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDTO.fromEntity(savedCategory);
    }

    @Transactional
    public CategoryResponseDTO uploadIcon(Long id, MultipartFile file) {
        Category category = getCategoryById(id);

        if (category.getIconUrl() != null) {
            cloudinaryService.deleteImage(category.getIconUrl());
        }

        String iconUrl = cloudinaryService.uploadImage(file, "categories");
        category.setIconUrl(iconUrl);

        Category savedCategory = categoryRepository.save(category);
        return CategoryResponseDTO.fromEntity(savedCategory);
    }

    @Transactional
    public void delete(Long id) {
        Category category = getCategoryById(id);

        if (category.getIconUrl() != null) {
            cloudinaryService.deleteImage(category.getIconUrl());
        }

        categoryRepository.delete(category);
    }
}