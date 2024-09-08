package mission.document;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "authentication")
@Data
@Builder
public class AuthenticationDocument {
    private ObjectId id;
    private ObjectId missionId;
    private ObjectId participantId;
    private String username;
    private String userEmail;
    private LocalDateTime date;
    private boolean completed;
    private String photoData;
    private String textData;
}
