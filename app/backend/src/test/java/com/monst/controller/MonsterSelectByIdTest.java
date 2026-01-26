package com.monst.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.test.web.servlet.MockMvc;

import com.monst.config.SecurityConfig;
import com.monst.dto.response.MonsterFullResponse;
import com.monst.dto.response.MonsterFullResponse.ImageData;
import com.monst.dto.response.MonsterFullResponse.NamedImage;
import com.monst.handler.GlobalExceptionHandler;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

@WebMvcTest(controllers = MonsterController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class MonsterSelectByIdTest {

        @Autowired
        MockMvc mockMvc;

        @MockBean
        MonsterService monsterService;

        @MockBean
        MonsterFullQueryService monsterFullQueryService;

        @Test
        @WithAnonymousUser
        @DisplayName("GET /monster/select/{id} 公開: 未認証でも 200")
        void selectById_public_200() throws Exception {

                // 最低限のダミー
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
}
