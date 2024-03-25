package mission.document;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Authentication {
    private LocalDateTime date;
    private boolean completed;
    private String photoData;
    private String textData;
}
