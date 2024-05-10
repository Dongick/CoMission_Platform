package mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mission.dto.mission.*;
import mission.mock.WithMockCustomUser;
import mission.service.MissionService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import java.util.List;

@WebMvcTest(MissionController.class)
@WithMockCustomUser
class MissionControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private MissionService missionService;
    @Test
    @DisplayName("createMission 매서드 : 성공")
    void createMission() throws Exception {
        MissionCreateRequest missionCreateRequest = new MissionCreateRequest();
        missionCreateRequest.setTitle("testTitle");
        missionCreateRequest.setDescription("testDescription");
        missionCreateRequest.setMinParticipants(1);
        missionCreateRequest.setDuration(1);
        missionCreateRequest.setFrequency("매일");

        String missionInfoJson = objectMapper.writeValueAsString(missionCreateRequest);

        MockMultipartFile missionInfoPart = new MockMultipartFile("missionInfo", "json", "application/json", missionInfoJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "filename.txt", "text/plain", "some xml".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/mission")
                        .file(missionInfoPart)
                        .file(photoData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("good"));

        verify(missionService).createMission(any(MissionCreateRequest.class), any(MultipartFile.class));
    }

    @Test
    @DisplayName("createMission 매서드 : minParticipants의 값이 1보다 작아서 실패")
    void createMission_VALIDATION_FAILED() throws Exception {
        MissionCreateRequest missionCreateRequest = new MissionCreateRequest();
        missionCreateRequest.setTitle("testTitle");
        missionCreateRequest.setDescription("testDescription");
        missionCreateRequest.setMinParticipants(0);
        missionCreateRequest.setDuration(1);
        missionCreateRequest.setFrequency("매일");

        String missionInfoJson = objectMapper.writeValueAsString(missionCreateRequest);

        MockMultipartFile missionInfoPart = new MockMultipartFile("missionInfo", "json", "application/json", missionInfoJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "filename.txt", "text/plain", "some xml".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/mission")
                        .file(missionInfoPart)
                        .file(photoData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("updateMission 매서드: 성공")
    void updateMission() throws Exception {
        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle("testAfterTitle");
        missionUpdateRequest.setDescription("testDescription");
        missionUpdateRequest.setMinParticipants(1);
        missionUpdateRequest.setDuration(1);
        missionUpdateRequest.setFrequency("매일");

        String missionInfoJson = objectMapper.writeValueAsString(missionUpdateRequest);

        MockMultipartFile missionInfoPart = new MockMultipartFile("missionInfo", "json", "application/json", missionInfoJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "filename.txt", "text/plain", "some xml".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/mission/{id}", "testTitle")
                        .file(missionInfoPart)
                        .file(photoData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(content().string("good"));

        verify(missionService).updateMission(any(MissionUpdateRequest.class), any(MultipartFile.class), eq("testTitle"));
    }

    @Test
    @DisplayName("updateMission 매서드: duration 값이 1보다 작아서 실패")
    void updateMission_VALIDATION_FAILED() throws Exception {
        MissionUpdateRequest missionUpdateRequest = new MissionUpdateRequest();
        missionUpdateRequest.setAfterTitle("testAfterTitle");
        missionUpdateRequest.setDescription("testDescription");
        missionUpdateRequest.setMinParticipants(1);
        missionUpdateRequest.setDuration(0);
        missionUpdateRequest.setFrequency("매일");

        String missionInfoJson = objectMapper.writeValueAsString(missionUpdateRequest);

        MockMultipartFile missionInfoPart = new MockMultipartFile("missionInfo", "json", "application/json", missionInfoJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "filename.txt", "text/plain", "some xml".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/mission/{id}", "testTitle")
                        .file(missionInfoPart)
                        .file(photoData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("missionInfo 매서드: 성공")
    void missionInfo() throws Exception{
        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11, 00);

        MissionInfoResponse missionInfoResponse = MissionInfoResponse.builder()
                .id("testId")
                .title("testTitle")
                .description("testDescription")
                .photoUrl("testPhotoUrl")
                .createdAt(now)
                .startDate(LocalDate.from(now))
                .deadline(LocalDate.from(now))
                .minParticipants(1)
                .participants(1)
                .duration(1)
                .status("STARTED")
                .frequency("매일")
                .username("testUserName")
                .creatorEmail("testCreatorEmail")
                .participant(Boolean.TRUE)
                .build();
        when(missionService.missionInfo("testTitle")).thenReturn(missionInfoResponse);

        mockMvc.perform(get("/api/mission/info/{id}", "testTitle")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.title").value("testTitle"))
                .andExpect(jsonPath("$.description").value("testDescription"))
                .andExpect(jsonPath("$.photoUrl").value("testPhotoUrl"))
                .andExpect(jsonPath("$.createdAt").value("2024-04-12T11:11:00"))
                .andExpect(jsonPath("$.startDate").value("2024-04-12"))
                .andExpect(jsonPath("$.deadline").value("2024-04-12"))
                .andExpect(jsonPath("$.minParticipants").value(1))
                .andExpect(jsonPath("$.participants").value(1))
                .andExpect(jsonPath("$.duration").value(1))
                .andExpect(jsonPath("$.status").value("STARTED"))
                .andExpect(jsonPath("$.frequency").value("매일"))
                .andExpect(jsonPath("$.username").value("testUserName"))
                .andExpect(jsonPath("$.creatorEmail").value("testCreatorEmail"))
                .andExpect(jsonPath("$.participant").value(Boolean.TRUE));

        verify(missionService).missionInfo("testTitle");
    }

    @Test
    @DisplayName("missionSearch 매서드: 성공")
    void missionSearch() throws Exception{
        MissionSearchRequest missionSearchRequest = new MissionSearchRequest();
        missionSearchRequest.setTitle("testTitle");

        List<MissionInfo> missionInfoList = new ArrayList<>();
        MissionSearchResponse missionSearchResponse = new MissionSearchResponse(missionInfoList);

        when(missionService.missionSearch(any(MissionSearchRequest.class))).thenReturn(missionSearchResponse);

        mockMvc.perform(post("/api/mission/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(missionSearchRequest))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));

        verify(missionService).missionSearch(any(MissionSearchRequest.class));
    }
}