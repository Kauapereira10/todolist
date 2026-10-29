package br.com.kauapereira.todolist.repository;

import br.com.kauapereira.todolist.model.Owner;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    Optional<Owner> findByToken (UUID token);
}
