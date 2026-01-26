package com.monst.controller;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.util.List;

import com.monst.dto.response.MonsterFullListResponse;
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
class MonsterSelectAllTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private MonsterService monsterService;

        @MockBean
        private MonsterFullQueryService monsterFullQueryService;

        @Test
        @WithMockUser
        @DisplayName("GET /monster/select/all デフォルト（includeImages省略=false）: 200 で base64 は任意（検証しない）")
        void selectAll_ok_default_includeImagesFalse() throws Exception {

                MonsterFullResponse item = sampleMonster(false);
                MonsterFullListResponse response = new MonsterFullListResponse(
                                List.of(item),
                                0,
                                20,
                                1L);

                when(monsterFullQueryService.selectAll(
                                eq("ルシ"),
                                eq(6),
                                eq(4L),
                                eq(13L),
                                eq(2L),
                                eq(0),
                                eq(20),
                                eq(false))).thenReturn(response);

                mockMvc.perform(
                                get("/monster/select/all")
                                                .param("q", "ルシ")
                                                .param("rarity", "6")
                                                .param("attributeId", "4")
                                                .param("tribeId", "13")
                                                .param("battleTypeId", "2")
                                                .param("page", "0")
                                                .param("size", "20")
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.page").value(0))
                                .andExpect(jsonPath("$.size").value(20))
                                .andExpect(jsonPath("$.total").value(1))
                                .andExpect(jsonPath("$.items[0].id").value(1))
                                .andExpect(jsonPath("$.items[0].number").value(10001))
                                .andExpect(jsonPath("$.items[0].name").value("ルシファー"))
                                // attribute は文字列ではなくオブジェクト
                                .andExpect(jsonPath("$.items[0].attribute.name").value("光"))
                                .andExpect(jsonPath("$.items[0].attribute.image.path")
                                                .value("uploads/masters/attribute/light.png"))
                                // モンスター画像も ImageData
                                .andExpect(jsonPath("$.items[0].images.icon.path")
                                                .value("uploads/monsters/icon/icon_xxx.png"))
                                .andExpect(jsonPath("$.items[0].friendshipCombo.main.power").value(123456));

                verify(monsterFullQueryService).selectAll(
                                eq("ルシ"),
                                eq(6),
                                eq(4L),
                                eq(13L),
                                eq(2L),
                                eq(0),
                                eq(20),
                                eq(false));
        }

        @Test
        @WithMockUser
        @DisplayName("GET /monster/select/all?includeImages=true: 200 で base64 を含む")
        void selectAll_ok_includeImagesTrue() throws Exception {

                MonsterFullResponse item = sampleMonster(true);
                MonsterFullListResponse response = new MonsterFullListResponse(
                                List.of(item),
                                0,
                                20,
                                1L);

                when(monsterFullQueryService.selectAll(
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(0),
                                eq(20),
                                eq(true))).thenReturn(response);

                mockMvc.perform(
                                get("/monster/select/all")
                                                .param("includeImages", "true")
                                                .accept(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(status().isOk())
                                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
                                .andExpect(jsonPath("$.items[0].images.icon.base64").value("BASE64_ICON"))
                                .andExpect(jsonPath("$.items[0].images.monster.base64").value("BASE64_MONSTER"))
                                .andExpect(jsonPath("$.items[0].attribute.image.base64").value("BASE64_ATTR"))
                                .andExpect(jsonPath("$.items[0].friendshipCombo.main.image.base64")
                                                .value("BASE64_FC_MAIN"));

                verify(monsterFullQueryService).selectAll(
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(null),
                                eq(0),
                                eq(20),
                                eq(true));
        }

        private MonsterFullResponse sampleMonster(boolean includeImages) {
                ImageData attrImg = new ImageData(
                                "uploads/masters/attribute/light.png",
                                "image/png",
                                includeImages ? "BASE64_ATTR" : null);

                ImageData luckImg = new ImageData(
                                "uploads/masters/luckSkill/critical.png",
                                "image/png",
                                includeImages ? "BASE64_LUCK" : null);

                ImageData fcMainImg = new ImageData(
                                "uploads/masters/friendshipName/energy_circle_l.png",
                                "image/png",
                                includeImages ? "BASE64_FC_MAIN" : null);

                ImageData iconImg = new ImageData(
                                "uploads/monsters/icon/icon_xxx.png",
                                "image/png",
                                includeImages ? "BASE64_ICON" : null);

                ImageData monsterImg = new ImageData(
                                "uploads/monsters/monster/monster_xxx.png",
                                "image/png",
                                includeImages ? "BASE64_MONSTER" : null);

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
                                new NamedImage("クリティカル", luckImg),
                                new MonsterFullResponse.Abilities(
                                                List.of(new MonsterFullResponse.Ability("アンチダメージウォール", null)),
                                                List.of()),
                                new MonsterFullResponse.ConnectSkill(
                                                "条件文",
                                                List.of(new MonsterFullResponse.Ability("バリア", null))),
                                new MonsterFullResponse.Skills("壁SSターン短縮", "回復"),
                                new MonsterFullResponse.StrikeShot("SS名", "SS効果"),
                                new MonsterFullResponse.FriendshipCombo(
                                                new MonsterFullResponse.FriendshipCombo.Friendship(
                                                                "エナジーサークルL",
                                                                new NamedImage("光", attrImg),
                                                                "サークル",
                                                                "近距離に強力なサークル攻撃",
                                                                123456,
                                                                fcMainImg),
                                                null),
                                "天使シリーズ",
                                new MonsterFullResponse.Images(iconImg, monsterImg));
        }
}
