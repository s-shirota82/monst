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

    @MockBean
    private MasterImageService masterImageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("GET /master/{type}/select/all admin: 200")
    void selectAll_ok_admin() throws Exception {

        when(masterService.selectAll(eq(MasterType.ATTRIBUTE)))
                .thenReturn(List.of(
                        Map.of("id", 1, "name", "火")
                ));

        mockMvc.perform(
                get("/master/{type}/select/all", "attribute")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE));

        verify(masterService).selectAll(eq(MasterType.ATTRIBUTE));
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("GET /master/{type}/select/all user: 403")
    void selectAll_forbidden_user() throws Exception {

        mockMvc.perform(
                get("/master/{type}/select/all", "attribute")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
        )
        .andExpect(status().isForbidden());
    }
}
