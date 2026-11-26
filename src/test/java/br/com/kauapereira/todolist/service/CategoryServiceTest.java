package br.com.kauapereira.todolist.service;

import br.com.kauapereira.todolist.dto.category.CategoryRequestDTO;
import br.com.kauapereira.todolist.dto.category.CategoryResponseDTO;
import br.com.kauapereira.todolist.exception.DuplicateResourceException;
import br.com.kauapereira.todolist.mappers.CategoryMapper;
import br.com.kauapereira.todolist.model.Category;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste para servico de categorias")
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("createCategory: normaliza o nome, salva e devolve o DTO do mapper")
    void createCategory_nameWithSpaces_trimsSavesAndReturnsDto() {
        // Arrange
        Owner owner = Owner.create();
        CategoryRequestDTO requestDTO = new CategoryRequestDTO(" Estudos ");
        CategoryResponseDTO expected = new CategoryResponseDTO(1L, "Estudos");

        when(categoryRepository.existsByNameAndOwnerId("Estudos", owner.getId())).thenReturn(false);
        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));
        when(categoryMapper.toDto(any(Category.class))).thenReturn(expected);

        // Act
        CategoryResponseDTO result = categoryService.createCategory(requestDTO, owner);

        // Assert
        assertSame(expected, result);

        ArgumentCaptor<Category> captor = ArgumentCaptor.forClass(Category.class);
        verify(categoryRepository).save(captor.capture());
        assertEquals("Estudos", captor.getValue().getName());
        assertSame(owner, captor.getValue().getOwner());

    }

    @Test
    @DisplayName("createCategory: lança conflito e não grava quando o nome já existe para o dono")
    void createCategory_nameDuplicate_dontSave() {
        Owner owner = Owner.create();
        CategoryRequestDTO requestDTO = new CategoryRequestDTO("Estudos ");
        when(categoryRepository.existsByNameAndOwnerId("Estudos", owner.getId())).thenReturn(true);

        // Act + Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class,
                () -> categoryService.createCategory(requestDTO, owner));

        assertEquals("Category already exists: Estudos", ex.getMessage());
        verify(categoryRepository, never()).save(any());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("getAllCategories: devolve os DTOs na mesma ordem em que o repository entregou")
    void getAllCategories_ownerWithCategories_returnsMappedListInOrder() {

        Owner owner = Owner.create();
        Category first = new Category("Estudos", owner);
        Category second = new Category("Trabalho", owner);
        CategoryResponseDTO firstDto = new CategoryResponseDTO(1L, "Estudos");
        CategoryResponseDTO secondDto = new CategoryResponseDTO(2L, "Trabalho");

        when(categoryRepository.findAllByOwnerIdOrderByNameAsc(owner.getId())).thenReturn(List.of(first, second));
        when(categoryMapper.toDto(first)).thenReturn(firstDto);
        when(categoryMapper.toDto(second)).thenReturn(secondDto);

        List<CategoryResponseDTO> result = categoryService.getAllCategories(owner);

        assertEquals(2, result.size());
        assertEquals(firstDto, result.get(0));
        assertEquals(secondDto, result.get(1));

    }

    @Test
    @DisplayName("getAllCategories: devolve lista vazia quando o dono não tem categorias")
    void getAllCategories_ownerWithoutCategories_returnsEmptyList() {
        Owner owner = Owner.create();

        when(categoryRepository.findAllByOwnerIdOrderByNameAsc(owner.getId())).thenReturn(List.of());

        List<CategoryResponseDTO> result = categoryService.getAllCategories(owner);

        assertTrue(result.isEmpty());
        verifyNoInteractions(categoryMapper);
    }

    @Test
    @DisplayName("getCategoryById: devolve o DTO quando a categoria é do dono")
    void getCategoryById_found_returnsDto() {
        Long id = 1L;
        Owner owner = Owner.create();
        Category category = new Category("Estudos", owner);
        CategoryResponseDTO expected = new CategoryResponseDTO(id, "Estudos");

        when(categoryRepository.findByIdAndOwnerId(id, owner.getId())).thenReturn(Optional.of(category));
        when(categoryMapper.toDto(category)).thenReturn(expected);

        CategoryResponseDTO result = categoryService.getCategoryById(id, owner);

        assertSame(expected, result);

    }

}