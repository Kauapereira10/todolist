package br.com.kauapereira.todolist.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CookieProperties cookieProperties;

    // Teste 1
    @Test
    @DisplayName("Deveria atribuir cookie de visitante em requisição sem cookie")
    void requestWithoutCookie_receivesVisitorCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie visitorCookie = result.getResponse().getCookie(cookieProperties.name());
        assertNotNull(visitorCookie);
        assertDoesNotThrow(() -> UUID.fromString(visitorCookie.getValue()));
        assertTrue(visitorCookie.isHttpOnly());
    }

    @Test
    @DisplayName("Não deveria gerar novo cookie quando enviar um cookie válido")
    void requestWithValidCookie_doesNotReceiveNewCookie() throws Exception {
        MvcResult first = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie visitorCookie = first.getResponse().getCookie(cookieProperties.name());

        mockMvc.perform(get("/api/tasks").cookie(visitorCookie))
                .andExpect(status().isOk())
                .andExpect(cookie().doesNotExist(cookieProperties.name()));
    }

    @Test
    @DisplayName("Deveria tratar cookie malformatado como um novo visitante")
    void requestWithMalformedCookie_isTreatedAsNewVisitor() throws Exception {
        Cookie forged = new Cookie(cookieProperties.name(), "malformed-uuid");

        MvcResult result = mockMvc.perform(get("/api/tasks").cookie(forged))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();

        Cookie issued = result.getResponse().getCookie(cookieProperties.name());
        assertNotNull(issued);
        assertNotEquals(forged.getValue(), issued.getValue());
    }

    @Test
    @DisplayName("Deveria tratar cookie com UUID inexistente como um novo visitante")
    void requestWithUnknownUuidCookie_isTreatedAsNewVisitor() throws Exception {
        Cookie forged = new Cookie(cookieProperties.name(), UUID.randomUUID().toString());

        MvcResult result = mockMvc.perform(get("/api/tasks").cookie(forged))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty())
                .andReturn();

        Cookie issued = result.getResponse().getCookie(cookieProperties.name());
        assertNotNull(issued);
        assertNotEquals(forged.getValue(), issued.getValue());
    }
}