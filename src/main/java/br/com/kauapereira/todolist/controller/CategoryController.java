package br.com.kauapereira.todolist.controller;

import br.com.kauapereira.todolist.config.OwnerInterceptor;
import br.com.kauapereira.todolist.dto.category.CategoryRequestDTO;
import br.com.kauapereira.todolist.dto.category.CategoryResponseDTO;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.service.CategoryService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(
            @Valid @RequestBody CategoryRequestDTO requestDTO,
            @RequestAttribute(OwnerInterceptor.OWNER_ATTRIBUTE) Owner owner
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createCategory(requestDTO, owner));
    }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories(
            @RequestAttribute(OwnerInterceptor.OWNER_ATTRIBUTE) Owner owner
    ) {
        return ResponseEntity.ok(service.getAllCategories(owner));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(
            @PathVariable("id") Long id,
            @RequestAttribute(OwnerInterceptor.OWNER_ATTRIBUTE) Owner owner
    ) {
        return ResponseEntity.ok(service.getCategoryById(id, owner));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> renameCategory(@PathVariable("id") Long id,
                                                              @Valid @RequestBody CategoryRequestDTO requestDTO,
                                                              @RequestAttribute(OwnerInterceptor.OWNER_ATTRIBUTE) Owner owner) {
        return ResponseEntity.ok().body(service.renameCategory(id, requestDTO, owner));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("id") Long id,
                                                              @RequestAttribute(OwnerInterceptor.OWNER_ATTRIBUTE) Owner owner) {
        service.deleteCategory(id, owner);
        return ResponseEntity.noContent().build();
    }


}