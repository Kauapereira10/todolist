package br.com.kauapereira.todolist.config;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OwnerInterceptorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CookieProperties cookieProperties;

    @Test
    void requestWithoutCookie_receivesVisitorCookie() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andReturn();

        Cookie cookie = result.getResponse().getCookie(cookieProperties.name());

        assertNotNull(cookie);
        assertDoesNotThrow(() -> UUID.fromString(cookie.getValue()));
        assertTrue(cookie.isHttpOnly());
    }

    @Test
    void requestWithValidCookie_doesNotReceiveNewCookie() throws Exception {

    }
}