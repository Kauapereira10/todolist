package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.category.CategoryRequestDTO;
import br.com.kauapereira.todolist.dto.category.CategoryResponseDTO;
import br.com.kauapereira.todolist.exception.DuplicateResourceException;
import br.com.kauapereira.todolist.mappers.CategoryMapper;
import br.com.kauapereira.todolist.model.Category;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.repository.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO, Owner owner) {
        String name = requestDTO.name().trim();

        if (repository.existsByNameAndOwnerId(name, owner.getId())) {
            throw new DuplicateResourceException("Category already exists: " + name);
        }

        Category savedCategory = repository.save(new Category(name, owner));
        return mapper.toDto(savedCategory);
    }

}
