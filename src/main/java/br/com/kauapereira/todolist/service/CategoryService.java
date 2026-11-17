package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.category.CategoryRequestDTO;
import br.com.kauapereira.todolist.dto.category.CategoryResponseDTO;
import br.com.kauapereira.todolist.exception.CategoryInUseException;
import br.com.kauapereira.todolist.exception.DuplicateResourceException;
import br.com.kauapereira.todolist.exception.ResourceNotFoundException;
import br.com.kauapereira.todolist.mappers.CategoryMapper;
import br.com.kauapereira.todolist.model.Category;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.repository.CategoryRepository;
import br.com.kauapereira.todolist.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@AllArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository repository;
    private final TaskRepository taskRepository;
    private final CategoryMapper mapper;

    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO, Owner owner) {
        String name = normalizeName(requestDTO.name());

        if (repository.existsByNameAndOwnerId(name, owner.getId())) {
            throw new DuplicateResourceException("Category already exists: " + name);
        }

        Category savedCategory = repository.save(new Category(name, owner));
        return mapper.toDto(savedCategory);
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories(Owner owner) {
        return repository.findAllByOwnerIdOrderByNameAsc(owner.getId()).stream()
                .map(mapper::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id, Owner owner) {
        Category category = findCategoryById(id, owner);
        return mapper.toDto(category);
    }

    @Transactional
    public CategoryResponseDTO renameCategory(Long id, CategoryRequestDTO requestDTO, Owner owner) {
        String name = normalizeName(requestDTO.name());

        Category category = findCategoryById(id, owner);

        if(repository.existsByNameAndOwnerIdAndIdNot(name, owner.getId(), id)) {
            throw new DuplicateResourceException("Category already exists: " + name);
        }

        category.rename(name);
        return mapper.toDto(repository.saveAndFlush(category));
    }

    @Transactional
    public void deleteCategory(Long id, Owner owner) {
        Category category = findCategoryById(id, owner);

        if (taskRepository.existsByCategoryIdAndOwner(id, owner)) {
            throw new CategoryInUseException("Category has tasks and cannot be deleted");
        }

        repository.delete(category);
    }

    private Category findCategoryById(Long id, Owner owner) {
        return repository.findByIdAndOwnerId(id, owner.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
    }

    private String normalizeName(String name) {
        return name.trim();
    }
}
