package br.com.kauapereira.todolist.repository;

import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("""
            SELECT t FROM Task t
            WHERE (:status IS NULL OR t.status = :status)
              AND (:priority IS NULL OR t.priority = :priority)
              AND t.owner = :owner
            """)
    List<Task> findAllByFilters(@Param("status") Status status,
                                @Param("priority") Priority priority,
                                @Param("owner") Owner owner);

    Optional<Task> findByIdAndOwner(Long id, Owner owner);
}
