package mission.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import mission.dto.authentication.AuthenticationCreateRequest;
import mission.dto.authentication.AuthenticationListResponse;
import mission.dto.authentication.AuthenticationUpdateRequest;
import mission.mock.WithMockCustomUser;
import mission.service.AuthenticationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthenticationController.class)
@WithMockCustomUser
class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private AuthenticationService authenticationService;

    @Test
    @DisplayName("createAuthentication 매서드: 성공")
    void createAuthentication() throws Exception{
        AuthenticationCreateRequest authenticationCreateRequest = new AuthenticationCreateRequest();
        authenticationCreateRequest.setTextData("testTextData");

        String authenticationJson = objectMapper.writeValueAsString(authenticationCreateRequest);

        MockMultipartFile textData = new MockMultipartFile("textData", "", "application/json", authenticationJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "photo.jpg", "image/jpeg", "photo data".getBytes());

        mockMvc.perform(multipart("/api/authentication/{id}", "testTitle")
                        .file(textData)
                        .file(photoData)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(content().string("good"));

        verify(authenticationService).createAuthentication(any(), any(), eq("testTitle"));
    }

    @Test
    @DisplayName("updateAuthentication 매서드: 성공")
    void updateAuthentication() throws Exception{
        AuthenticationUpdateRequest authenticationUpdateRequest = new AuthenticationUpdateRequest();
        authenticationUpdateRequest.setTextData("testTextData");

        String authenticationJson = objectMapper.writeValueAsString(authenticationUpdateRequest);


        MockMultipartFile textData = new MockMultipartFile("textData", "", "application/json", authenticationJson.getBytes(StandardCharsets.UTF_8));
        MockMultipartFile photoData = new MockMultipartFile("photoData", "photo.jpg", "image/jpeg", "photo data".getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/authentication/{id}", "testTitle")
                        .file(textData)
                        .file(photoData)
                        .with(request -> { request.setMethod("PUT"); return request; })
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("good"));

        verify(authenticationService).updateAuthentication(any(), any(), eq("testTitle"));
    }

    @Test
    @DisplayName("deleteAuthentication 매서드: 성공")
    void deleteAuthentication() throws Exception{
        mockMvc.perform(delete("/api/authentication/{id}", "testTitle")
                .with(csrf()))
                .andExpect(status().isPartialContent())
                .andExpect(content().string("good"));

        verify(authenticationService).deleteAuthentication("testTitle");
    }

//    @Test
//    @DisplayName("authenticationList 매서드: 성공")
//    void authenticationList() throws Exception{
//        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11, 00);
//
//        List<Map<String, Object>> authenticationData = new ArrayList<>();
//        Map<String ,Object> authenticationMap = new HashMap<>();
//        authenticationMap.put("date", now);
//        authenticationMap.put("photoData", "testPhotoData");
//        authenticationMap.put("textData", "testTextData");
//        authenticationMap.put("userEmail", "testUserEmail");
//        authenticationMap.put("username", "testUsername");
//        authenticationData.add(authenticationMap);
//
//        AuthenticationListResponse response = new AuthenticationListResponse(authenticationData);
//        when(authenticationService.authenticationList(anyString(), anyInt())).thenReturn(response);
//
//        mockMvc.perform(get("/api/authentication/{id}/{num}", "testTitle", "0"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.authenticationData[0].photoData").value("testPhotoData"))
//                .andExpect(jsonPath("$.authenticationData[0].textData").value("testTextData"))
//                .andExpect(jsonPath("$.authenticationData[0].userEmail").value("testUserEmail"))
//                .andExpect(jsonPath("$.authenticationData[0].username").value("testUsername"))
//                .andExpect(jsonPath("$.authenticationData[0].date").value("2024-04-12T11:11:00"));
//
//        verify(authenticationService).authenticationList("testTitle", 0);
//    }
}