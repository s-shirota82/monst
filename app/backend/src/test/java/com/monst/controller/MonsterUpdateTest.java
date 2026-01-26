package com.monst.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Objects;

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
@Import(com.monst.handler.GlobalExceptionHandler.class)
class MonsterUpdateTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MonsterService monsterService;

    // MonsterControllerの依存を満たすため
    @MockBean
    private MonsterFullQueryService monsterFullQueryService;

    @Test
    @WithMockUser
    @DisplayName("PUT /monster/update/{id} 正常系（dataのみ、画像無し）で 200 と id を返す")
    void update_ok_withoutImages() throws Exception {

        when(monsterService.update(eq(1L), any(MonsterCreateRequest.class), any(), any()))
                .thenReturn(1L);

        String json = """
                {
                  "number": 10001,
                  "rarity": 6,
                  "name": "更新後モンスター",
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

        RequestPostProcessor csrfRpp = Objects.requireNonNull(csrf());

        mockMvc.perform(
                multipart("/monster/update/{id}", 1L)
                        .file(dataPart)
                        // multipart はデフォルトPOSTなのでPUTに上書き
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .with(csrfRpp)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(1));

        verify(monsterService).update(eq(1L), any(MonsterCreateRequest.class), any(), any());
    }

    @Test
    @WithMockUser
    @DisplayName("PUT /monster/update/{id} 正常系（data + 画像2枚）で 200 と id を返す")
    void update_ok_withImages() throws Exception {

        when(monsterService.update(eq(1L), any(MonsterCreateRequest.class), any(), any()))
                .thenReturn(1L);

        String json = """
                {
                  "number": 10001,
                  "rarity": 6,
                  "name": "更新後モンスター",
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

        RequestPostProcessor csrfRpp = Objects.requireNonNull(csrf());

        mockMvc.perform(
                multipart("/monster/update/{id}", 1L)
                        .file(dataPart)
                        .file(iconImage)
                        .file(monsterImage)
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        })
                        .with(csrfRpp)
                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));

        verify(monsterService).update(eq(1L), any(MonsterCreateRequest.class), any(), any());
    }
}
