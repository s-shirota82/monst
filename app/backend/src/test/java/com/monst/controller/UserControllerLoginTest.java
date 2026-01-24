package com.monst.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.dto.response.LoginResponse;
import com.monst.handler.GlobalExceptionHandler;
import com.monst.service.AuthService;
import com.monst.service.UserService;
import com.monst.service.exception.UnauthorizedException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Security を無効化（login の挙動だけ見る）
@Import(GlobalExceptionHandler.class) // 統一エラー形式を検証する
class UserControllerLoginTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    AuthService authService;

    // UserController が依存しているので MockBean は必要（使わなくてもOK）
    @MockBean
    UserService userService;

    @Test
    void login_invalidEmail_returns400_withUnifiedErrorResponse() throws Exception {
        String body = """
                {
                  "email": "not-an-email",
                  "password": "Abcd1234"
                }
                """;

        mockMvc.perform(post("/user/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='email')]").exists());
    }

    @Test
    void login_invalidPassword_returns400_withUnifiedErrorResponse() throws Exception {
        // 例：大文字を含まない（あなたの password ルール違反）
        String body = """
                {
                  "email": "test@example.com",
                  "password": "abcd1234"
                }
                """;

        mockMvc.perform(post("/user/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isNotEmpty())
                .andExpect(jsonPath("$.details[?(@.field=='password')]").exists());
    }

    @Test
    void login_unauthorized_returns401_withUnifiedErrorResponse() throws Exception {
        when(authService.login(any()))
                .thenThrow(new UnauthorizedException());

        String body = """
                {
                  "email": "test@example.com",
                  "password": "Abcd1234"
                }
                """;

        mockMvc.perform(post("/user/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.message").exists())
                .andExpect(jsonPath("$.details").isArray())
                .andExpect(jsonPath("$.details").isEmpty());
    }

    @Test
    void login_success_returns200_andLoginResponse() throws Exception {
        // LoginResponse は id/email に統一している前提
        when(authService.login(any()))
                .thenReturn(new LoginResponse(1L, "test@example.com"));

        String body = """
                {
                  "email": "test@example.com",
                  "password": "Abcd1234"
                }
                """;

        MvcResult result = mockMvc.perform(post("/user/login")
                .contentType(APPLICATION_JSON_VALUE)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andReturn();

        String json = result.getResponse().getContentAsString();
        assertNotNull(json);
        assertFalse(json.isBlank(), "response body should not be blank");

        JsonNode root = objectMapper.readTree(json);

        // 成功レスポンスに code があるなら ErrorResponse を返している
        assertFalse(root.has("code"), "success response must not be ErrorResponse");

        assertTrue(root.hasNonNull("id"), "response should have field 'id'");
        assertEquals(1L, root.get("id").asLong());

        assertTrue(root.hasNonNull("email"), "response should have field 'email'");
        assertEquals("test@example.com", root.get("email").asText());
    }
}
