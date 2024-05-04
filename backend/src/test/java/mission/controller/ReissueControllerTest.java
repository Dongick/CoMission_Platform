package mission.controller;

import jakarta.servlet.http.Cookie;
import mission.mock.WithMockCustomUser;
import mission.service.ReissueService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReissueController.class)
@WithMockCustomUser
class ReissueControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReissueService reissueService;

    @Test
    @DisplayName("reissue 매서드: 성공")
    void reissue() throws Exception {

        mockMvc.perform(post("/api/reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(new Cookie("RefreshToken", "dummyRefreshToken"))
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("good"));
    }
}