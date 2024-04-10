package mission.util;

import java.time.LocalDateTime;

public class TimeProvider {
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
