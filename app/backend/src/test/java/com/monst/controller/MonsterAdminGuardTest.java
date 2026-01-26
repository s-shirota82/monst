package com.monst.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import com.monst.config.SecurityConfig;
import com.monst.dto.response.MonsterFullListResponse;
import com.monst.dto.response.MonsterFullResponse;
import com.monst.dto.response.MonsterFullResponse.ImageData;
import com.monst.dto.response.MonsterFullResponse.NamedImage;
import com.monst.handler.GlobalExceptionHandler;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

@WebMvcTest(controllers = MonsterController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class MonsterAdminGuardTest {

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
        @WithAnonymousUser
        @DisplayName("GET /monster/select/all は公開（未認証でも 200）")
        void selectAll_public() throws Exception {

                when(monsterFullQueryService.selectAll(
                                any(), any(), any(), any(), any(),
                                any(Integer.class), any(Integer.class)))
                                .thenReturn(new MonsterFullListResponse(List.of(), 0, 20, 0L));

                mockMvc.perform(get("/monster/select/all"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithAnonymousUser
        @DisplayName("GET /monster/select/{id} は公開（未認証でも 200）")
        void selectById_public() throws Exception {

                ImageData img = new ImageData("uploads/x.png", "image/png", null);

                MonsterFullResponse body = new MonsterFullResponse(
                                1L,
                                10001,
                                "dummy",
                                new MonsterFullResponse.Rarity(6, 99),
                                new MonsterFullResponse.EvolutionStage("獣神化", true, true),
                                new NamedImage("光", img),
                                "反射",
                                "ドラゴン",
                                "バランス",
                                new MonsterFullResponse.Status(
                                                new MonsterFullResponse.Status.Stat(1, 1),
                                                new MonsterFullResponse.Status.Stat(1, 1),
                                                new MonsterFullResponse.Status.Speed(new BigDecimal("1.0"),
                                                                new BigDecimal("1.0"))),
                                null,
                                new MonsterFullResponse.Abilities(List.of(), List.of()),
                                new MonsterFullResponse.ConnectSkill(null, List.of()),
                                new MonsterFullResponse.Skills(null, null),
                                new MonsterFullResponse.StrikeShot(null, null),
                                new MonsterFullResponse.FriendshipCombo(
                                                new MonsterFullResponse.FriendshipCombo.Friendship(
                                                                "fc",
                                                                new NamedImage("光", img),
                                                                "cat",
                                                                "desc",
                                                                null,
                                                                img),
                                                null),
                                null,
                                new MonsterFullResponse.Images(img, img));

                when(monsterFullQueryService.selectById(1L)).thenReturn(body);

                mockMvc.perform(get("/monster/select/1"))
                                .andExpect(status().isOk());
        }

        @Test
        @WithMockUser(roles = "USER")
        @DisplayName("POST /monster/register は admin 以外 403（CSRF含む）")
        void register_forbidden_for_user() throws Exception {

                MockMultipartFile data = new MockMultipartFile(
                                "data", "data.json", MediaType.APPLICATION_JSON_VALUE,
                                """
                                                {"number":1,"rarity":1,"name":"a","evolutionStageId":1,"attributeId":1,
                                                 "hpMax":1,"hpPlusMax":1,"attackMax":1,"attackPlusMax":1,"speedMax":1.0,"speedPlusMax":1.0,
                                                 "hitTypeId":1,"tribeId":1,"battleTypeId":1,"strikeShotNameId":1,"strikeShotEffectId":1,
                                                 "friendshipComboId":1,"specialNote":0}
                                                """
                                                .getBytes());

                MockMultipartFile icon = new MockMultipartFile("iconImage", "a.png", "image/png", "x".getBytes());
                MockMultipartFile monster = new MockMultipartFile("monsterImage", "b.png", "image/png", "y".getBytes());

                mockMvc.perform(
                                multipart("/monster/register")
                                                .file(data).file(icon).file(monster)
                                                .with(csrfRpp()))
                                .andExpect(status().isForbidden());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        @DisplayName("POST /monster/register は admin なら 201（CSRF含む）")
        void register_allowed_for_admin() throws Exception {

                when(monsterService.create(any(), any(), any())).thenReturn(1L);

                MockMultipartFile data = new MockMultipartFile(
                                "data", "data.json", MediaType.APPLICATION_JSON_VALUE,
                                """
                                                {"number":1,"rarity":1,"name":"a","evolutionStageId":1,"attributeId":1,
                                                 "hpMax":1,"hpPlusMax":1,"attackMax":1,"attackPlusMax":1,"speedMax":1.0,"speedPlusMax":1.0,
                                                 "hitTypeId":1,"tribeId":1,"battleTypeId":1,"strikeShotNameId":1,"strikeShotEffectId":1,
                                                 "friendshipComboId":1,"specialNote":0}
                                                """
                                                .getBytes());

                MockMultipartFile icon = new MockMultipartFile("iconImage", "a.png", "image/png", "x".getBytes());
                MockMultipartFile monster = new MockMultipartFile("monsterImage", "b.png", "image/png", "y".getBytes());

                mockMvc.perform(
                                multipart("/monster/register")
                                                .file(data).file(icon).file(monster)
                                                .with(csrfRpp()))
                                .andExpect(status().isCreated());
        }
}
