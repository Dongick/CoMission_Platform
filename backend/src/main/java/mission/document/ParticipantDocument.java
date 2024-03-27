package mission.document;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.time.LocalDateTime;

@Document(collection = "participant")
@Data
@Builder
public class ParticipantDocument {
    @Id
    private ObjectId id;
    private ObjectId missionId;
    private String userEmail;
    private String username;
    private LocalDateTime joinedAt;
    private List<Authentication> authentication;
}
