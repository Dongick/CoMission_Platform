package mission.service;

import lombok.RequiredArgsConstructor;
import mission.document.Authentication;
import mission.document.MissionDocument;
import mission.document.ParticipantDocument;
import mission.dto.mission.MissionCreateRequest;
import mission.enums.MissionStatus;
import mission.repository.MissionRepository;
import mission.repository.ParticipantRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public void createMission(MissionCreateRequest missionCreateRequest) {
        if (missionCreateRequest == null || missionCreateRequest.getMinParticipants() <= 0) {
            // 에러 처리: 요청이 유효하지 않은 경우
            throw new IllegalArgumentException("Invalid mission create request");
        }

        LocalDateTime now = LocalDateTime.now();

        MissionDocument missionDocument;

        if (missionCreateRequest.getMinParticipants() == 1) {
            LocalDate deadline = now.toLocalDate().plusDays(missionCreateRequest.getDuration());

            missionDocument = missionRepository.save(MissionDocument.builder()
                    .createdAt(now)
                    .creatorEmail(missionCreateRequest.getCreatorEmail())
                    .duration(missionCreateRequest.getDuration())
                    .deadline(deadline)
                    .description(missionCreateRequest.getDescription())
                    .title(missionCreateRequest.getTitle())
                    .frequency(missionCreateRequest.getFrequency())
                    .status(MissionStatus.STARTED.name())
                    .startDate(LocalDate.from(now))
                    .minParticipants(missionCreateRequest.getMinParticipants())
                    .build());

            List<Authentication> authenticationList = new ArrayList<>();

            Authentication authentication = Authentication.builder()
                    .date(LocalDate.from(now))
                    .completed(false)
                    .photoData(null)
                    .textData(null)
                    .build();

            authenticationList.add(authentication);

            participantRepository.save(ParticipantDocument.builder()
                    .missionId(missionDocument.getId())
                    .joinedAt(now)
                    .userEmail(missionCreateRequest.getCreatorEmail())
                    .authentication(authenticationList)
                    .build());

        } else {
            missionDocument = missionRepository.save(MissionDocument.builder()
                    .createdAt(now)
                    .creatorEmail(missionCreateRequest.getCreatorEmail())
                    .duration(missionCreateRequest.getDuration())
                    .deadline(null)
                    .description(missionCreateRequest.getDescription())
                    .title(missionCreateRequest.getTitle())
                    .frequency(missionCreateRequest.getFrequency())
                    .status(MissionStatus.CREATED.name())
                    .startDate(null)
                    .minParticipants(missionCreateRequest.getMinParticipants())
                    .build());

            participantRepository.save(ParticipantDocument.builder()
                    .missionId(missionDocument.getId())
                    .joinedAt(now)
                    .userEmail(missionCreateRequest.getCreatorEmail())
                    .authentication(new ArrayList<>())
                    .build());
        }
    }

}
