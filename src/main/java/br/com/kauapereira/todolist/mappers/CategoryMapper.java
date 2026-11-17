package br.com.kauapereira.todolist.mappers;

import br.com.kauapereira.todolist.dto.category.CategoryResponseDTO;
import br.com.kauapereira.todolist.model.Category;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    CategoryResponseDTO toDto(Category category);
}
