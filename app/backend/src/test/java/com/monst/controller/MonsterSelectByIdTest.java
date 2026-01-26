package com.monst.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import com.monst.dto.response.MonsterFullResponse;
import com.monst.dto.response.MonsterFullResponse.ImageData;
import com.monst.dto.response.MonsterFullResponse.NamedImage;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MonsterController.class)
@Import(com.monst.handler.GlobalExceptionHandler.class)
class MonsterSelectByIdTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MonsterService monsterService;

        @MockBean
        private MonsterFullQueryService monsterFullQueryService;

        @Test
        @WithMockUser
        @DisplayName("GET /monster/select/{id} 正常系: 200 で base64 を含む")
        void selectById_ok() throws Exception {

                MonsterFullResponse response = sampleMonsterDetail();

                when(monsterFullQueryService.selectById(eq(1L))).thenReturn(response);

                mockMvc.perform(
                                get("/monster/select/{id}", 1L)
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.number").value(10001))
                                .andExpect(jsonPath("$.name").value("ルシファー"))
                                .andExpect(jsonPath("$.attribute.name").value("光"))
                                .andExpect(jsonPath("$.attribute.image.base64").value("BASE64_ATTR"))
                                .andExpect(jsonPath("$.images.icon.base64").value("BASE64_ICON"))
                                .andExpect(jsonPath("$.friendshipCombo.main.image.base64").value("BASE64_FC_MAIN"));

                verify(monsterFullQueryService).selectById(eq(1L));
        }

        private MonsterFullResponse sampleMonsterDetail() {
                ImageData attrImg = new ImageData(
                                "uploads/masters/attribute/light.png",
                                "image/png",
                                "BASE64_ATTR");

                ImageData iconImg = new ImageData(
                                "uploads/monsters/icon/icon_xxx.png",
                                "image/png",
                                "BASE64_ICON");

                ImageData monsterImg = new ImageData(
                                "uploads/monsters/monster/monster_xxx.png",
                                "image/png",
                                "BASE64_MONSTER");

                ImageData fcMainImg = new ImageData(
                                "uploads/masters/friendshipName/energy_circle_l.png",
                                "image/png",
                                "BASE64_FC_MAIN");

                return new MonsterFullResponse(
                                1L,
                                10001,
                                "ルシファー",
                                new MonsterFullResponse.Rarity(6, 99),
                                new MonsterFullResponse.EvolutionStage("獣神化", true, true),
                                new NamedImage("光", attrImg),
                                "反射",
                                "ドラゴン",
                                "バランス",
                                new MonsterFullResponse.Status(
                                                new MonsterFullResponse.Status.Stat(25000, 27500),
                                                new MonsterFullResponse.Status.Stat(28000, 31000),
                                                new MonsterFullResponse.Status.Speed(new BigDecimal("350.123"),
                                                                new BigDecimal("380.456"))),
                                null,
                                new MonsterFullResponse.Abilities(List.of(), List.of()),
                                new MonsterFullResponse.ConnectSkill(null, List.of()),
                                new MonsterFullResponse.Skills(null, null),
                                new MonsterFullResponse.StrikeShot(null, null),
                                new MonsterFullResponse.FriendshipCombo(
                                                new MonsterFullResponse.FriendshipCombo.Friendship(
                                                                "エナジーサークルL",
                                                                new NamedImage("光", attrImg),
                                                                "サークル",
                                                                "説明",
                                                                123456,
                                                                fcMainImg),
                                                null),
                                null,
                                new MonsterFullResponse.Images(iconImg, monsterImg));
        }
}
