package com.monst.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.monst.config.SecurityConfig;
import com.monst.handler.GlobalExceptionHandler;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

@WebMvcTest(controllers = MonsterController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class MonsterUpdateTest {

        @Autowired
        MockMvc mockMvc;

        @MockBean
        MonsterService monsterService;

        @MockBean
        MonsterFullQueryService monsterFullQueryService;

        private static RequestPostProcessor csrfRpp() {
                return Objects.requireNonNull(csrf(), "csrf must not be null");
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("PUT /monster/update/{id} 権限不足（user）: 403（CSRF含む）")
        void update_forbidden_for_user() throws Exception {

                MockMultipartFile data = new MockMultipartFile(
                                "data", "data.json", MediaType.APPLICATION_JSON_VALUE,
                                """
                                                {"number":1,"rarity":1,"name":"a","evolutionStageId":1,"attributeId":1,
                                                 "hpMax":1,"hpPlusMax":1,"attackMax":1,"attackPlusMax":1,"speedMax":1.0,"speedPlusMax":1.0,
                                                 "hitTypeId":1,"tribeId":1,"battleTypeId":1,"strikeShotNameId":1,"strikeShotEffectId":1,
                                                 "friendshipComboId":1,"specialNote":0}
                                                """
                                                .getBytes());

                mockMvc.perform(
                                multipart("/monster/update/{id}", 1L)
                                                .file(data)
                                                .with(req -> {
                                                        req.setMethod("PUT"); // multipart はデフォルト POST のため PUT に上書き
                                                        return req;
                                                })
                                                .with(csrfRpp()))
                                .andExpect(status().isForbidden());
        }
}
