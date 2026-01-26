package com.monst.master;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MasterController.class)
@Import(com.monst.handler.GlobalExceptionHandler.class)
class MasterSelectAllTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MasterService masterService;

    @Test
    @WithMockUser
    @DisplayName("GET /master/{type}/select/all 正常系: 200 と配列を返す")
    void selectAll_ok() throws Exception {

        when(masterService.selectAll(eq(MasterType.ATTRIBUTE)))
                .thenReturn(List.of(
                        Map.of("id", 1, "name", "火"),
                        Map.of("id", 2, "name", "水")
                ));

        mockMvc.perform(
                get("/master/{type}/select/all", "attribute")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$[0].id").value(1))
        .andExpect(jsonPath("$[0].name").value("火"));

        verify(masterService).selectAll(eq(MasterType.ATTRIBUTE));
    }
}
