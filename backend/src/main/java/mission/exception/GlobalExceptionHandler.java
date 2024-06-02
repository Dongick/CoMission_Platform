package mission.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException ex) {
        log.error("[BadRequestException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorCode().getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MissionAuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(final MissionAuthenticationException ex) {
        log.error("[MissionAuthenticationException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorCode().getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(final NotFoundException ex) {
        log.error("[NotFoundException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorCode().getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(final ConflictException ex) {
        log.error("[ConflictException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorCode().getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleForbiddenException(final ForbiddenException ex) {
        log.error("[ForbiddenException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ex.getErrorCode(), ex.getErrorCode().getMessage());

        return ResponseEntity.status(ex.getHttpStatus()).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        log.error("[MethodArgumentNotValidException] cause: {}, message: {}",ErrorCode.VALIDATION_FAILED,ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.VALIDATION_FAILED, ErrorCode.VALIDATION_FAILED.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("[MethodArgumentTypeMismatchException] cause: {}, message: {}",ex.getErrorCode(),ex.getMessage());
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.TYPE_MISMATCH_FAILED, ErrorCode.TYPE_MISMATCH_FAILED.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}
