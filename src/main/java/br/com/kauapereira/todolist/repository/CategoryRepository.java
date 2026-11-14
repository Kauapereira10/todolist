package br.com.kauapereira.todolist.repository;

import br.com.kauapereira.todolist.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByIdAndOwnerId(Long id, Long ownerId);

    List<Category> findAllByOwnerIdOrderByNameAsc(Long ownerId);

    boolean existsByNameAndOwnerId(String name, Long ownerId);

    boolean existsByNameAndOwnerIdAndIdNot(String name, Long ownerId, Long id);
}