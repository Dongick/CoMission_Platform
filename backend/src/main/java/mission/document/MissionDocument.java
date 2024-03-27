package mission.document;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Data
@Document(collection = "mission")
@Builder
public class MissionDocument {
    @Id
    private ObjectId id;
    private String title;
    private String description;
<<<<<<< HEAD
=======
    private String photoUrl;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
    private LocalDateTime createdAt;
    private LocalDate startDate;
    private LocalDate deadline;
    private int minParticipants;
    private int participants;
    private int duration;
    private String status;
    private String frequency;
    private String creatorEmail;
}
