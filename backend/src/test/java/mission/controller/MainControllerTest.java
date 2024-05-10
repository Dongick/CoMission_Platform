package mission.controller;

import mission.dto.main.MainResponse;
import mission.dto.mission.MissionInfo;
import mission.mock.WithMockCustomUser;
import mission.service.MainService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

@WebMvcTest(MainController.class)
@WithMockCustomUser
class MainControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private MainService mainService;

    @Test
    @DisplayName("getMainList 매서드: default 파라미터인 상태로 성공")
    public void getMainListDefaultParameter() throws Exception{
        //given
        List<MissionInfo> missionInfoList1 = prepareMissionInfo1();
        List<MissionInfo> missionInfoList2 = prepareMissionInfo2();

        MainResponse mockResponse = new MainResponse(missionInfoList1, missionInfoList2);

        when(mainService.getMainMissionList("latest", 0, "all")).thenReturn(mockResponse);

        //when
        mockMvc.perform(get("/api/main")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantMissionInfoList[0].id").value("testId"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].username").value("testUser"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].creatorEmail").value("testEmail"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].title").value("testTitle"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].photoUrl").value("testUrl"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].minParticipants").value(1))
                .andExpect(jsonPath("$.participantMissionInfoList[0].participants").value(2))
                .andExpect(jsonPath("$.participantMissionInfoList[0].duration").value(3))
                .andExpect(jsonPath("$.participantMissionInfoList[0].status").value("STARTED"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].frequency").value("매일"))
                .andExpect(jsonPath("$.missionInfoList[0].id").value("test2Id"))
                .andExpect(jsonPath("$.missionInfoList[0].username").value("test2User"))
                .andExpect(jsonPath("$.missionInfoList[0].creatorEmail").value("test2Email"))
                .andExpect(jsonPath("$.missionInfoList[0].title").value("test2Title"))
                .andExpect(jsonPath("$.missionInfoList[0].photoUrl").value("test2Url"))
                .andExpect(jsonPath("$.missionInfoList[0].minParticipants").value(1))
                .andExpect(jsonPath("$.missionInfoList[0].participants").value(2))
                .andExpect(jsonPath("$.missionInfoList[0].duration").value(3))
                .andExpect(jsonPath("$.missionInfoList[0].status").value("STARTED"))
                .andExpect(jsonPath("$.missionInfoList[0].frequency").value("매일"));

        //then
        verify(mainService).getMainMissionList("latest", 0, "all");
    }

    @Test
    @DisplayName("getMainList 매서드: 파라미터가 존재하는 상태로 성공")
    public void getMainListWithParameter() throws Exception{
        //given
        List<MissionInfo> missionInfoList1 = prepareMissionInfo1();
        List<MissionInfo> missionInfoList2 = prepareMissionInfo2();

        MainResponse mockResponse = new MainResponse(missionInfoList1, missionInfoList2);

        when(mainService.getMainMissionList("participants", 0, "completed")).thenReturn(mockResponse);

        //when
        mockMvc.perform(get("/api/main")
                .param("sort", "participants")
                .param("num", "0")
                .param("filter", "completed")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participantMissionInfoList[0].id").value("testId"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].username").value("testUser"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].creatorEmail").value("testEmail"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].title").value("testTitle"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].photoUrl").value("testUrl"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].minParticipants").value(1))
                .andExpect(jsonPath("$.participantMissionInfoList[0].participants").value(2))
                .andExpect(jsonPath("$.participantMissionInfoList[0].duration").value(3))
                .andExpect(jsonPath("$.participantMissionInfoList[0].status").value("STARTED"))
                .andExpect(jsonPath("$.participantMissionInfoList[0].frequency").value("매일"))
                .andExpect(jsonPath("$.missionInfoList[0].id").value("test2Id"))
                .andExpect(jsonPath("$.missionInfoList[0].username").value("test2User"))
                .andExpect(jsonPath("$.missionInfoList[0].creatorEmail").value("test2Email"))
                .andExpect(jsonPath("$.missionInfoList[0].title").value("test2Title"))
                .andExpect(jsonPath("$.missionInfoList[0].photoUrl").value("test2Url"))
                .andExpect(jsonPath("$.missionInfoList[0].minParticipants").value(1))
                .andExpect(jsonPath("$.missionInfoList[0].participants").value(2))
                .andExpect(jsonPath("$.missionInfoList[0].duration").value(3))
                .andExpect(jsonPath("$.missionInfoList[0].status").value("STARTED"))
                .andExpect(jsonPath("$.missionInfoList[0].frequency").value("매일"));

        //then
        verify(mainService).getMainMissionList("participants", 0, "completed");
    }

    @Test
    @DisplayName("getMainList 매서드: 잘못된 타입의 파라미터로 인한 에러")
    public void getMainList_InvalidTypeParameter_BadRequest() throws Exception {
        mockMvc.perform(get("/api/main")
                        .param("num", "not_a_number")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private List<MissionInfo> prepareMissionInfo1() {
        List<MissionInfo> missionInfoList = new ArrayList<>();

        MissionInfo missionInfo = new MissionInfo();
        missionInfo.setId("testId");
        missionInfo.setUsername("testUser");
        missionInfo.setCreatorEmail("testEmail");
        missionInfo.setTitle("testTitle");
        missionInfo.setPhotoUrl("testUrl");
        missionInfo.setMinParticipants(1);
        missionInfo.setParticipants(2);
        missionInfo.setDuration(3);
        missionInfo.setStatus("STARTED");
        missionInfo.setFrequency("매일");

        missionInfoList.add(missionInfo);

        return missionInfoList;
    }

    private List<MissionInfo> prepareMissionInfo2() {
        List<MissionInfo> missionInfoList = new ArrayList<>();

        MissionInfo missionInfo = new MissionInfo();
        missionInfo.setId("test2Id");
        missionInfo.setUsername("test2User");
        missionInfo.setCreatorEmail("test2Email");
        missionInfo.setTitle("test2Title");
        missionInfo.setPhotoUrl("test2Url");
        missionInfo.setMinParticipants(1);
        missionInfo.setParticipants(2);
        missionInfo.setDuration(3);
        missionInfo.setStatus("STARTED");
        missionInfo.setFrequency("매일");

        missionInfoList.add(missionInfo);

        return missionInfoList;
    }
}