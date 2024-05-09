package mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mission.dto.participant.ParticipantRequest;
import mission.mock.WithMockCustomUser;
import mission.service.ParticipantService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ParticipantController.class)
@WithMockCustomUser
class ParticipantControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ParticipantService participantService;

    @Test
    @DisplayName("participantMission 매서드 : 성공")
    void participantMission() throws Exception {
        //given
        ParticipantRequest participantRequest = new ParticipantRequest();
        participantRequest.setId("testId");

        doNothing().when(participantService).participateMission(any(ParticipantRequest.class));

        // when & then
        mockMvc.perform(post("/api/participant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(participantRequest))
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("good"));
    }

    @Test
    @DisplayName("participantMission 매서드 : 잘못된 request 값으로 인해 실패")
    public void participantMission_InvalidRequest_BadRequest() throws Exception {
    // given
    String invalidRequestBody = "{}";

    // when & then
    mockMvc.perform(post("/api/participant")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidRequestBody)
            .with(csrf()))
            .andExpect(status().isBadRequest());
    }
}