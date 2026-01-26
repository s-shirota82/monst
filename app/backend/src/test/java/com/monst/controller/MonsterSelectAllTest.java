package com.monst.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
import com.monst.dto.response.MonsterFullListResponse;
import com.monst.handler.GlobalExceptionHandler;
import com.monst.service.MonsterFullQueryService;
import com.monst.service.MonsterService;

@WebMvcTest(controllers = MonsterController.class)
@Import({ SecurityConfig.class, GlobalExceptionHandler.class })
class MonsterSelectAllTest {

        @Autowired
        MockMvc mockMvc;

        @MockBean
        MonsterService monsterService;

        @MockBean
        MonsterFullQueryService monsterFullQueryService;

        @Test
        @WithAnonymousUser
        @DisplayName("GET /monster/select/all 公開: 未認証でも 200")
        void selectAll_public_200() throws Exception {

                when(monsterFullQueryService.selectAll(any(), any(), any(), any(), any(), anyInt(), anyInt()))
                                .thenReturn(new MonsterFullListResponse(List.of(), 0, 20, 0L));

                mockMvc.perform(get("/monster/select/all"))
                                .andExpect(status().isOk());
        }
}
