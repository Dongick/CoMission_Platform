package mission.exception;

import org.springframework.http.HttpStatus;

public class NotFoundException extends RuntimeException{
    private static final HttpStatus httpStatus = HttpStatus.NOT_FOUND;
    private final ErrorCode errorCode;

    public NotFoundException(ErrorCode errorCode, String message) {
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
