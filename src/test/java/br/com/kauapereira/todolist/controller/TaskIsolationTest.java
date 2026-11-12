package br.com.kauapereira.todolist.controller;

import br.com.kauapereira.todolist.config.CookieProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("Testes de Isolamento de Tarefas entre Visitantes")
class TaskIsolationTest {

    private static final String DUE_DATE = "2099-12-31";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CookieProperties cookieProperties;

    @Test
    @DisplayName("Dono deve conseguir ler sua própria tarefa")
    void owner_canReadOwnTask() throws Exception {
        Cookie visitorA = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(get("/api/tasks/{id}", id).cookie(visitorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.title").value("tarefa do A"));
    }

    @Test
    @DisplayName("Visitante B não deve conseguir ler tarefa pertencente ao Visitante A (Retorna 404)")
    void visitorB_cannotReadTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(get("/api/tasks/{id}", id).cookie(visitorB))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Visitante B não deve conseguir atualizar dados completos de tarefa do Visitante A")
    void visitorB_cannotUpdateTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        String invasion = """
                {"title": "invadida pelo B", "priority": "LOW", "dueDate": "2098-01-01"}
                """;

        mockMvc.perform(put("/api/tasks/{id}", id)
                        .cookie(visitorB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invasion))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Visitante B não deve conseguir alterar o status de tarefa do Visitante A")
    void visitorB_cannotChangeStatusOfTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(patch("/api/tasks/{id}/status", id)
                        .cookie(visitorB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"status": "COMPLETED"}
                                """))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Visitante B não deve conseguir alterar a prioridade de tarefa do Visitante A")
    void visitorB_cannotChangePriorityOfTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(patch("/api/tasks/{id}/priority", id)
                        .cookie(visitorB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"priority": "LOW"}
                                """))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Visitante B não deve conseguir alterar a data de vencimento de tarefa do Visitante A")
    void visitorB_cannotChangeDueDateOfTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(patch("/api/tasks/{id}/dueDate", id)
                        .cookie(visitorB)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"dueDate": "2098-01-01"}
                                """))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Visitante B não deve conseguir deletar tarefa pertencente ao Visitante A")
    void visitorB_cannotDeleteTaskOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        long id = createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(delete("/api/tasks/{id}", id).cookie(visitorB))
                .andExpect(status().isNotFound());

        assertTaskUntouched(visitorA, id);
    }

    @Test
    @DisplayName("Listagem geral do Visitante B não deve conter tarefas do Visitante A")
    void visitorB_listDoesNotContainTasksOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        createTask(visitorA, "tarefa do A", "HIGH");
        createTask(visitorB, "tarefa do B", "LOW");

        mockMvc.perform(get("/api/tasks").cookie(visitorB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("tarefa do B"));
    }

    @Test
    @DisplayName("Filtros de busca do Visitante B não devem vazar tarefas do Visitante A")
    void visitorB_filterDoesNotLeakTasksOfVisitorA() throws Exception {
        Cookie visitorA = newVisitor();
        Cookie visitorB = newVisitor();
        createTask(visitorA, "tarefa do A", "HIGH");
        createTask(visitorB, "tarefa do B", "LOW");

        mockMvc.perform(get("/api/tasks")
                        .param("priority", "HIGH")
                        .param("status", "TO_DO")
                        .cookie(visitorB))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        mockMvc.perform(get("/api/tasks")
                        .param("priority", "HIGH")
                        .param("status", "TO_DO")
                        .cookie(visitorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @ParameterizedTest(name = "cookie forjado: {0}")
    @ValueSource(strings = {
            "malformed-uuid",
            "12345",
            "00000000-0000-0000-0000-000000000000"
    })
    @DisplayName("Cookie forjado não deveria dar acesso a tarefas de outro visitante")
    void forgedCookie_doesNotLeakTasksOfOtherVisitor(String forgedValue) throws Exception {
        Cookie visitorA = newVisitor();
        createTask(visitorA, "tarefa do A", "HIGH");

        mockMvc.perform(get("/api/tasks").cookie(visitorA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));

        Cookie forged = new Cookie(cookieProperties.name(), forgedValue);

        MvcResult result = mockMvc.perform(get("/api/tasks").cookie(forged))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)))
                .andReturn();

        Cookie issued = result.getResponse().getCookie(cookieProperties.name());
        assertNotNull(issued);
        assertNotEquals(forgedValue, issued.getValue());
    }

    private Cookie newVisitor() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();
        return result.getResponse().getCookie(cookieProperties.name());
    }

    private long createTask(Cookie visitor, String title, String priority) throws Exception {
        String body = """
                {"title": "%s", "priority": "%s", "dueDate": "%s"}
                """.formatted(title, priority, DUE_DATE);

        String json = mockMvc.perform(post("/api/tasks")
                        .cookie(visitor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(json).get("id").asLong();
    }

    private void assertTaskUntouched(Cookie owner, long id) throws Exception {
        mockMvc.perform(get("/api/tasks/{id}", id).cookie(owner))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("tarefa do A"))
                .andExpect(jsonPath("$.priority").value("HIGH"))
                .andExpect(jsonPath("$.status").value("TO_DO"))
                .andExpect(jsonPath("$.dueDate").value(DUE_DATE));
    }
}