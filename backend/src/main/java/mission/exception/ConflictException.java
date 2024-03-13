package mission.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends RuntimeException{
    private static final HttpStatus httpStatus = HttpStatus.CONFLICT;
    private final ErrorCode errorCode;

    public ConflictException(ErrorCode errorCode, String message) {
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
