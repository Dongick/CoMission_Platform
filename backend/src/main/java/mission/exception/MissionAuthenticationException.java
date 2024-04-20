package mission.exception;

import org.springframework.http.HttpStatus;

public class MissionAuthenticationException extends RuntimeException{
    private static final HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
    private final ErrorCode errorCode;

    public MissionAuthenticationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
