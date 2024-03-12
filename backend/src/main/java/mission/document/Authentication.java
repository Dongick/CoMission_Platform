package mission.document;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Authentication {
    private LocalDate date;
    private boolean completed;
    private String photoData;
    private String textData;
}
