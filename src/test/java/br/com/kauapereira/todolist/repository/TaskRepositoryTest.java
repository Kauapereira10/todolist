package br.com.kauapereira.todolist.repository;

import br.com.kauapereira.todolist.config.JpaAuditingConfig;
import br.com.kauapereira.todolist.model.Owner;
import br.com.kauapereira.todolist.model.Task;
import br.com.kauapereira.todolist.model.enums.Priority;
import br.com.kauapereira.todolist.model.enums.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
@Import(JpaAuditingConfig.class)
class TaskRepositoryTest {

    private static final LocalDate DUE_DATE = LocalDate.of(2099, 12, 31);

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OwnerRepository ownerRepository;

    private Owner ownerA;
    private Owner ownerB;

    @BeforeEach
    void setUp() {
        ownerA = ownerRepository.save(Owner.create());
        ownerB = ownerRepository.save(Owner.create());
    }

    @Test
    @DisplayName("Deveria encontrar a tarefa quando o id e o dono coincidem")
    void findByIdAndOwner_correctOwner_returnsTask() {
        Task task = saveTask(ownerA, "Tarefa A", Status.IN_PROGRESS, Priority.HIGH);
        flushAndClear();

        Optional<Task> found = taskRepository.findByIdAndOwner(task.getId(), ownerA);
        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Tarefa A");
        assertThat(found.get().getOwner().getId()).isEqualTo(ownerA.getId());
    }

    @Test
    @DisplayName("Não deveria encontrar a tarefa quando ela pertence a outro dono")
    void findByIdAndOwner_wrongOwner_returnsEmpty() {
        Task task = saveTask(ownerA, "Tarefa A", Status.IN_PROGRESS, Priority.HIGH);
        flushAndClear();

        Optional<Task> found = taskRepository.findByIdAndOwner(task.getId(), ownerB);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Não deveria encontrar tarefa com id inexistente")
    void findByIdAndOwner_unknownId_returnsEmpty() {
        saveTask(ownerA, "Tarefa A", Status.IN_PROGRESS, Priority.HIGH);
        flushAndClear();

        Optional<Task> found = taskRepository.findByIdAndOwner(Long.MAX_VALUE, ownerA);
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("Sem filtros, deveria listar somente as tarefas do dono")
    void findAllByFilters_noFilters_returnsOnlyOwnerTasks() {
        saveTask(ownerA, "Tarefa A1", Status.IN_PROGRESS, Priority.HIGH);
        saveTask(ownerA, "Tarefa A2", Status.COMPLETED, Priority.LOW);
        saveTask(ownerB, "Tarefa B1", Status.IN_PROGRESS, Priority.MEDIUM);
        flushAndClear();

        List<Task> tasks = taskRepository.findAllByFilters(null, null, null, ownerA);
        assertThat(tasks).hasSize(2);
    }

    @Test
    @DisplayName("Com filtro de status, deveria listar só as do dono com aquele status")
    void findAllByFilters_statusFilter_returnsOnlyMatchingOwnerTasks() {
        saveTask(ownerA, "Tarefa A1", Status.IN_PROGRESS, Priority.HIGH);
        saveTask(ownerA, "Tarefa A2", Status.COMPLETED, Priority.LOW);
        saveTask(ownerB, "Tarefa B1", Status.IN_PROGRESS, Priority.MEDIUM);
        flushAndClear();

        List<Task> tasks = taskRepository.findAllByFilters(Status.IN_PROGRESS, null, null, ownerA);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactly("Tarefa A1");
    }

    @Test
    @DisplayName("Com filtro de prioridade, deveria listar só as do dono com aquela prioridade")
    void findAllByFilters_priorityFilter_returnsOnlyMatchingOwnerTasks() {
        saveTask(ownerA, "A-high", Status.TO_DO, Priority.HIGH);
        saveTask(ownerA, "A-low", Status.TO_DO, Priority.LOW);
        saveTask(ownerB, "B-high", Status.TO_DO, Priority.HIGH);
        flushAndClear();

        List<Task> tasks = taskRepository.findAllByFilters(null, Priority.HIGH, null, ownerA);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactly("A-high");
    }

    @Test
    @DisplayName("Com status e prioridade juntos, deveria aplicar os dois filtros")
    void findAllByFilters_statusAndPriority_appliesBothFilters() {
        saveTask(ownerA, "A-match", Status.IN_PROGRESS, Priority.MEDIUM);
        saveTask(ownerA, "A-wrong-status", Status.TO_DO, Priority.MEDIUM);
        saveTask(ownerA, "A-wrong-priority", Status.IN_PROGRESS, Priority.LOW);
        saveTask(ownerB, "B-match", Status.IN_PROGRESS, Priority.MEDIUM);
        flushAndClear();

        List<Task> tasks = taskRepository.findAllByFilters(Status.IN_PROGRESS, Priority.MEDIUM, null, ownerA);
        assertThat(tasks).extracting(Task::getTitle)
                .containsExactly("A-match");

    }

    @Test
    @DisplayName("Dono sem tarefas deveria receber lista vazia, mesmo com tarefas de outros no banco")
    void findAllByFilters_ownerWithoutTasks_returnsEmpty() {
        saveTask(ownerB, "B1", Status.TO_DO, Priority.HIGH);
        saveTask(ownerB, "B2", Status.COMPLETED, Priority.LOW);
        flushAndClear();

        List<Task> tasks = taskRepository.findAllByFilters(null, null, null, ownerA);
        assertThat(tasks).isEmpty();

    }

    private Task saveTask(Owner owner,String title, Status status, Priority priority) {
        Task task = new Task();
        task.setTitle(title);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(DUE_DATE);
        task.setOwner(owner);
        return taskRepository.save(task);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

}
