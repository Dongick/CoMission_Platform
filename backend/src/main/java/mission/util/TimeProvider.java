package mission.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TimeProvider {
    public LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now();
    }
}
