package com.monst.handler;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.monst.controller.UserController;
import com.monst.dto.response.RegisterResponse;
import com.monst.service.AuthService;
import com.monst.service.UserService;
import com.monst.service.exception.EmailAlreadyUsedException;
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
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class GlobalExceptionHandlerTest {

        @Autowired
        MockMvc mockMvc;

        @Autowired
        ObjectMapper objectMapper;

        @MockBean
        AuthService authService;

        @MockBean
        UserService userService;

        // ---------------- validation ----------------

        @Test
        void validationError_returns400_andUnifiedJsonShape() throws Exception {
                String body = """
                                {
                                  "email": "not-an-email",
                                  "password": "abcd1234",
                                  "name": "test"
                                }
                                """;

                mockMvc.perform(post("/user/register")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(body))
                                .andExpect(status().isBadRequest())
                                .andExpect(content().contentTypeCompatibleWith("application/json"))
                                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"))
                                .andExpect(jsonPath("$.message").value("Validation failed"))
                                .andExpect(jsonPath("$.details").isArray())
                                .andExpect(jsonPath("$.details").isNotEmpty())
                                .andExpect(jsonPath("$.details[?(@.field=='email')]").exists())
                                .andExpect(jsonPath("$.details[?(@.field=='password')]").exists());
        }

        // ---------------- 409 conflict ----------------

        @Test
        void emailAlreadyUsed_returns409_andUnifiedJsonShape() throws Exception {
                when(userService.register(any()))
                                .thenThrow(new EmailAlreadyUsedException());

                String body = """
                                {
                                  "email": "test@example.com",
                                  "password": "Abcd1234",
                                  "name": "test"
                                }
                                """;

                mockMvc.perform(post("/user/register")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(body))
                                .andExpect(status().isConflict())
                                .andExpect(content().contentTypeCompatibleWith("application/json"))
                                .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_USED"))
                                .andExpect(jsonPath("$.message").exists())
                                .andExpect(jsonPath("$.details").isArray())
                                .andExpect(jsonPath("$.details").isEmpty());
        }

        // ---------------- 401 unauthorized ----------------

        @Test
        void unauthorized_returns401_andUnifiedJsonShape() throws Exception {
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

        // ---------------- success ----------------

        @Test
        void register_success_returns201() throws Exception {
                when(userService.register(any()))
                                .thenReturn(new RegisterResponse(1L, "test@example.com", "test"));

                String body = """
                                {
                                  "email": "test@example.com",
                                  "password": "Abcd1234",
                                  "name": "test"
                                }
                                """;

                MvcResult result = mockMvc.perform(post("/user/register")
                                .contentType(APPLICATION_JSON_VALUE)
                                .content(body))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentTypeCompatibleWith("application/json"))
                                .andReturn();

                String json = result.getResponse().getContentAsString();
                assertNotNull(json);
                assertFalse(json.isBlank(), "response body should not be blank");

                JsonNode root = objectMapper.readTree(json);

                // ここで「本当に id/email/name が返っている」ことを保証する
                assertTrue(root.hasNonNull("id"), "response should have field 'id'");
                assertEquals(1L, root.get("id").asLong());

                assertTrue(root.hasNonNull("email"), "response should have field 'email'");
                assertEquals("test@example.com", root.get("email").asText());

                assertTrue(root.hasNonNull("name"), "response should have field 'name'");
                assertEquals("test", root.get("name").asText());
        }
}
