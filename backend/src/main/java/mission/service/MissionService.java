package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.*;
import mission.dto.oauth2.CustomOAuth2User;
import mission.enums.MissionStatus;
import mission.exception.*;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.bson.types.ObjectId;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MissionService {
    private final MissionRepository missionRepository;
    private final ParticipantRepository participantRepository;
    private final FileService fileService;

    // 미션 생성 매서드
    @Transactional
    public void createMission(MissionCreateRequest missionCreateRequest, MultipartFile photoData) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        Optional<MissionDocument> optionalMissionDocument = missionRepository.findByTitle(missionCreateRequest.getTitle());

        // 해당 미션과 같은 제목을 가진 미션이 존재하는지 확인
        if(optionalMissionDocument.isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_MISSION_NAME, ErrorCode.DUPLICATE_MISSION_NAME.getMessage());
        }

        LocalDateTime now = LocalDateTime.now();

        // 미션 생성 사진을 서버에 저장
        String fileLocation = photoData == null || photoData.isEmpty() ? null : fileService.uploadMissionFile(photoData);

        MissionDocument missionDocument = saveMission(missionCreateRequest, fileLocation, now, userEmail);

        saveParticipant(missionDocument.getId(), now, userEmail);
    }

    // 미션 수정 매서드
    @Transactional
    public void updateMission(MissionUpdateRequest missionUpdateRequest, MultipartFile photoData, String title) throws IOException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
        String userEmail = customOAuth2User.getEmail();

        MissionDocument missionDocument = getMissionDocument(title);

        // 해당 미션의 상태 확인
        if(missionDocument.getStatus().equals(MissionStatus.STARTED.name())) {
            throw new ConflictException(ErrorCode.MISSION_ALREADY_STARTED, ErrorCode.MISSION_ALREADY_STARTED.getMessage());
        } else if(missionDocument.getStatus().equals(MissionStatus.COMPLETED.name())) {
            throw new BadRequestException(ErrorCode.MISSION_ALREADY_COMPLETED, ErrorCode.MISSION_ALREADY_COMPLETED.getMessage());
        }

        Optional<MissionDocument> optionalMissionDocument = missionRepository.findByTitle(missionUpdateRequest.getAfterTitle());

        // 새로 바꾼 미션 제목이 기존에 존재하는지 확인
        if(optionalMissionDocument.isPresent()) {
            throw new ConflictException(ErrorCode.DUPLICATE_MISSION_NAME, ErrorCode.DUPLICATE_MISSION_NAME.getMessage());
        }

        // 미션을 수정하는 사용자가 해당 미션의 작성자와 동일한지 확인
        if(missionDocument.getCreatorEmail().equals(userEmail)) {

            // 기존 미션에 존재하는 사진 서버에서 삭제
            fileService.deleteFile(missionDocument.getPhotoUrl());

            // 미션 사진 서버에 저장
            String fileLocation = photoData == null || photoData.isEmpty() ? null : fileService.uploadMissionFile(photoData);

            LocalDateTime now = LocalDateTime.now();

            missionDocument.setTitle(missionUpdateRequest.getAfterTitle());
            missionDocument.setDescription(missionUpdateRequest.getDescription());
            missionDocument.setPhotoUrl(fileLocation);
            missionDocument.setDuration(missionUpdateRequest.getDuration());
            missionDocument.setMinParticipants(missionUpdateRequest.getMinParticipants());
            missionDocument.setFrequency(missionUpdateRequest.getFrequency());
            missionDocument.setStatus(missionUpdateRequest.getMinParticipants() == 1 ? MissionStatus.STARTED.name() : MissionStatus.CREATED.name());
            missionDocument.setStartDate(missionUpdateRequest.getMinParticipants() == 1 ? LocalDate.from(now) : null);
            missionDocument.setDeadline(missionUpdateRequest.getMinParticipants() == 1 ? now.toLocalDate().plusDays(missionUpdateRequest.getDuration()) : null);

            missionRepository.save(missionDocument);
        } else {
            throw new ForbiddenException(ErrorCode.MISSION_MODIFICATION_NOT_ALLOWED, ErrorCode.MISSION_MODIFICATION_NOT_ALLOWED.getMessage());
        }
    }

    // 미션 상세정보 매서드
    @Transactional
    public MissionInfoResponse missionInfo(String title) {
        Boolean participant = false;

        MissionDocument missionDocument = getMissionDocument(title);

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomOAuth2User) {
            CustomOAuth2User customOAuth2User = (CustomOAuth2User) principal;
            String userEmail = customOAuth2User.getEmail();

            Optional<ParticipantDocument> optionalParticipantDocument = participantRepository.findByMissionIdAndUserEmail(missionDocument.getId(), userEmail);

            // 해당 미션에 참여한 사용자인지 확인
            if(optionalParticipantDocument.isPresent()) {
                participant = true;
            }
        }

        return MissionInfoResponse.builder()
                .title(missionDocument.getTitle())
                .description(missionDocument.getDescription())
                .minParticipants(missionDocument.getMinParticipants())
                .participants(missionDocument.getParticipants())
                .frequency(missionDocument.getFrequency())
                .duration(missionDocument.getDuration())
                .status(missionDocument.getStatus())
                .deadline(missionDocument.getDeadline())
                .participant(participant)
                .build();
    }

    public MissionSearchResponse missionSearch(MissionSearchRequest missionSearchRequest) {
        List<MissionInfo> missionInfoList = missionRepository.findByTitleAndStatusNotContainingIgnoreCaseOrderByCreatedAtDesc(missionSearchRequest.getTitle());

        MissionSearchResponse missionSearchResponse = new MissionSearchResponse(missionInfoList);

        return missionSearchResponse;

    }

    // 미션 저장
    private MissionDocument saveMission(MissionCreateRequest request, String fileLocation, LocalDateTime now, String userEmail) {
        MissionDocument missionDocument = MissionDocument.builder()
                .createdAt(now)
                .creatorEmail(userEmail)
                .duration(request.getDuration())
                .photoUrl(fileLocation)
                .deadline(request.getMinParticipants() == 1 ? now.toLocalDate().plusDays(request.getDuration()) : null)
                .description(request.getDescription())
                .title(request.getTitle())
                .frequency(request.getFrequency())
                .status(request.getMinParticipants() == 1 ? MissionStatus.STARTED.name() : MissionStatus.CREATED.name())
                .startDate(request.getMinParticipants() == 1 ? LocalDate.from(now) : null)
                .minParticipants(request.getMinParticipants())
                .participants(1)
                .build();

        return missionRepository.save(missionDocument);
    }

    // 미션 참여자 저장
    private void saveParticipant(ObjectId missionId, LocalDateTime now, String userEmail) {
        participantRepository.save(ParticipantDocument.builder()
                .missionId(missionId)
                .joinedAt(now)
                .userEmail(userEmail)
                .authentication(new ArrayList<>())
                .build());
    }

    // 미션 종료
    private void endMission(MissionDocument mission) {
        // 미션을 종료 상태로 변경
        mission.setStatus(MissionStatus.COMPLETED.name());
        missionRepository.save(mission);
    }

    // 해당 미션이 존재하는지 확인
    private MissionDocument getMissionDocument(String title) {
        return missionRepository.findByTitle(title)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MISSION_NOT_FOUND, ErrorCode.MISSION_NOT_FOUND.getMessage()));
    }

    // 매일 미션이 종료되는지 확인
    @Scheduled(cron = "0 0 0 * * *") // 매일 자정에 실행
    @Transactional
    public void dailyAuthentications() {

        LocalDateTime now = LocalDateTime.now();

        // 모든 미션 가져오기
        List<MissionDocument> missions = missionRepository.findByStatus(MissionStatus.STARTED.name());

        for (MissionDocument mission : missions) {

            if(!(mission.getDeadline().isAfter(now.toLocalDate()))) {

                endMission(mission);
            }
        }
    }
}