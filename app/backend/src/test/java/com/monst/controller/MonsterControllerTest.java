package com.monst.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

import com.monst.config.SecurityConfig;
import com.monst.dto.request.MonsterCreateRequest;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

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

@WebMvcTest(controllers = MonsterController.class)
@Import({ SecurityConfig.class, com.monst.handler.GlobalExceptionHandler.class })
class MonsterControllerTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MonsterService monsterService;

        @MockBean
        private MonsterFullQueryService monsterFullQueryService;

        private static RequestPostProcessor csrfRpp() {
                return Objects.requireNonNull(csrf(), "csrf must not be null");
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /monster/register 正常系（admin）: 201 と id を返す")
        void register_ok_admin() throws Exception {

                when(monsterService.create(any(MonsterCreateRequest.class), any(), any()))
                                .thenReturn(123L);

                String json = """
                                {
                                  "number": 10001,
                                  "rarity": 6,
                                  "name": "テストモンスター",
                                  "evolutionStageId": 5,
                                  "attributeId": 1,
                                  "hpMax": 25000,
                                  "hpPlusMax": 27500,
                                  "attackMax": 28000,
                                  "attackPlusMax": 31000,
                                  "speedMax": 350.123,
                                  "speedPlusMax": 380.456,
                                  "hitTypeId": 1,
                                  "tribeId": 13,
                                  "battleTypeId": 2,
                                  "strikeShotNameId": 1,
                                  "strikeShotEffectId": 1,
                                  "friendshipComboId": 100,
                                  "specialNote": 0
                                }
                                """;

                MockMultipartFile dataPart = new MockMultipartFile(
                                "data",
                                "data.json",
                                MediaType.APPLICATION_JSON_VALUE,
                                json.getBytes());

                MockMultipartFile iconImage = new MockMultipartFile(
                                "iconImage",
                                "icon.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy-icon".getBytes());

                MockMultipartFile monsterImage = new MockMultipartFile(
                                "monsterImage",
                                "monster.png",
                                MediaType.IMAGE_PNG_VALUE,
                                "dummy-monster".getBytes());

                mockMvc.perform(
                                multipart("/monster/register")
                                                .file(dataPart)
                                                .file(iconImage)
                                                .file(monsterImage)
                                                .with(csrfRpp())
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.id").value(123));

                verify(monsterService).create(any(MonsterCreateRequest.class), any(), any());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("POST /monster/register 権限不足（user）: 403")
        void register_forbidden_user() throws Exception {

                String json = """
                                {
                                  "number": 10001,
                                  "rarity": 6,
                                  "name": "テストモンスター",
                                  "evolutionStageId": 5,
                                  "attributeId": 1,
                                  "hpMax": 25000,
                                  "hpPlusMax": 27500,
                                  "attackMax": 28000,
                                  "attackPlusMax": 31000,
                                  "speedMax": 350.123,
                                  "speedPlusMax": 380.456,
                                  "hitTypeId": 1,
                                  "tribeId": 13,
                                  "battleTypeId": 2,
                                  "strikeShotNameId": 1,
                                  "strikeShotEffectId": 1,
                                  "friendshipComboId": 100,
                                  "specialNote": 0
                                }
                                """;

                MockMultipartFile dataPart = new MockMultipartFile(
                                "data", "data.json", MediaType.APPLICATION_JSON_VALUE, json.getBytes());
                MockMultipartFile iconImage = new MockMultipartFile(
                                "iconImage", "icon.png", MediaType.IMAGE_PNG_VALUE, "x".getBytes());
                MockMultipartFile monsterImage = new MockMultipartFile(
                                "monsterImage", "monster.png", MediaType.IMAGE_PNG_VALUE, "x".getBytes());

                mockMvc.perform(
                                multipart("/monster/register")
                                                .file(dataPart)
                                                .file(iconImage)
                                                .file(monsterImage)
                                                .with(csrfRpp())
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isForbidden());
        }
}
