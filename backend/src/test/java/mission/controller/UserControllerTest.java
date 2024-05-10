package mission.controller;

import mission.dto.mission.SimpleMissionInfo;
import mission.dto.user.UserMissionPost;
import mission.dto.user.UserMissionPostResponse;
import mission.dto.user.UserPostResponse;
import mission.mock.WithMockCustomUser;
import mission.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(UserController.class)
@WithMockCustomUser
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    @Test
    @DisplayName("logout 매서드: 성공")
    void logout() throws Exception{
        mockMvc.perform(post("/api/user/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("good"));
    }

    @Test
    @DisplayName("getUserPost 매서드: 성공")
    void getUserPost() throws Exception{
        List<SimpleMissionInfo> simpleMissionInfoList = new ArrayList<>();
        SimpleMissionInfo simpleMissionInfo = new SimpleMissionInfo();
        simpleMissionInfo.setId("testId");
        simpleMissionInfo.setTitle("testTitle");
        simpleMissionInfo.setPhotoUrl("testPhotoUrl");

        simpleMissionInfoList.add(simpleMissionInfo);

        UserPostResponse response = new UserPostResponse(simpleMissionInfoList);
        when(userService.userPost()).thenReturn(response);

        mockMvc.perform(get("/api/user/post")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.simpleMissionInfoList[0].id").value("testId"))
                .andExpect(jsonPath("$.simpleMissionInfoList[0].title").value("testTitle"))
                .andExpect(jsonPath("$.simpleMissionInfoList[0].photoUrl").value("testPhotoUrl"));

    }

    @Test
    @DisplayName("getUserMissionAuthentication 매서드: 성공")
    void getUserMissionAuthentication() throws Exception{
        String email = "test@example.com";
        String missionId = "missionId";
        LocalDateTime now = LocalDateTime.of(2024, 4, 12, 11, 11, 00);
        List<UserMissionPost> userMissionPostList = new ArrayList<>();
        UserMissionPost userMissionPost = UserMissionPost.builder()
                .date(now)
                .photoData("testPhotoData")
                .textData("testTextData")
                .build();

        userMissionPostList.add(userMissionPost);
        UserMissionPostResponse response = new UserMissionPostResponse(userMissionPostList);

        when(userService.userMissionPost(email, missionId, 0)).thenReturn(response);

        mockMvc.perform(get("/api/user/{email}/mission/{id}/post", email, missionId)
                .param("num", "0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userMissionPostList[0].date").value("2024-04-12T11:11:00"))
                .andExpect(jsonPath("$.userMissionPostList[0].photoData").value("testPhotoData"))
                .andExpect(jsonPath("$.userMissionPostList[0].textData").value("testTextData"));
    }
}