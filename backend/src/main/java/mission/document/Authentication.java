package mission.document;

import lombok.Builder;
import lombok.Data;

<<<<<<< HEAD
import java.time.LocalDate;
=======
import java.time.LocalDateTime;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c

@Data
@Builder
public class Authentication {
<<<<<<< HEAD
    private LocalDate date;
=======
    private LocalDateTime date;
>>>>>>> 5a194e4b974ce7a70ddaa1fe0b0c2f51d42cec2c
    private boolean completed;
    private String photoData;
    private String textData;
}
