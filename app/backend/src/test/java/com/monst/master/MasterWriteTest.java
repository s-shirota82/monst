package com.monst.master;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;
import org.springframework.lang.NonNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

@WebMvcTest(controllers = MasterController.class)
@Import({ com.monst.config.SecurityConfig.class, com.monst.handler.GlobalExceptionHandler.class })
class MasterWriteTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MasterService masterService;

        @MockBean
        private MasterImageService masterImageService;

        // ★ Null type safety 完全対応：@NonNull を明示
        private static final @NonNull RequestPostProcessor CSRF = Objects.requireNonNull(csrf(),
                        "csrf RequestPostProcessor must not be null");

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /master/{type}/register 正常系: 201 と id を返す")
        void register_ok() throws Exception {

                when(masterService.register(eq(MasterType.ATTRIBUTE), any()))
                                .thenReturn(10L);

                mockMvc.perform(
                                post("/master/{type}/register", "attribute")
                                                .with(CSRF)
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                                .content("{\"name\":\"新属性\"}"))
                                .andExpect(status().isCreated())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.id").value(10));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("PUT /master/{type}/update/{id} 正常系: 200 と id を返す")
        void update_ok() throws Exception {

                when(masterService.update(eq(MasterType.ATTRIBUTE), eq(1L), any()))
                                .thenReturn(1L);

                mockMvc.perform(
                                put("/master/{type}/update/{id}", "attribute", 1L)
                                                .with(CSRF)
                                                .contentType(MediaType.APPLICATION_JSON_VALUE)
                                                .accept(MediaType.APPLICATION_JSON_VALUE)
                                                .content("{\"name\":\"属性名変更\"}"))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.id").value(1));
        }
}
