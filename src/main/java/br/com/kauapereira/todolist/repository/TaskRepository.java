package br.com.kauapereira.todolist.repository;

import br.com.kauapereira.todolist.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

}
