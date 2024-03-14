package mission.exception;

public enum ErrorCode {

    // MAIN



    // PARTICIPANT
    MISSION_ALREADY_COMPLETED("해당 미션이 이미 종료되었습니다."),
    MISSION_NOT_FOUND("해당 미션이 존재하지 않습니다."),
    ALREADY_PARTICIPATED("이미 해당 미션에 참여한 상태입니다."),


    // MISSION
    DUPLICATE_MISSION_NAME("이미 해당 이름의 미션이 존재합니다."),
    MISSION_MODIFICATION_NOT_ALLOWED("해당 미션을 만든 사용자가 아니라 수정할 권한이 없습니다."),
    MISSION_ALREADY_STARTED("해당 미션은 이미 시작되었습니다."),


    // AUTHENTICATION
    PARTICIPANT_NOT_FOUND("해당 미션에 참여한 사용자가 아닙니다."),
    DUPLICATE_AUTHENTICATION("이미 인증글을 작성했습니다."),
    AUTHENTICATION_NOT_FOUND("해당 인증글이 존재하지 않습니다."),
    MISSION_NOT_STARTED("해당 미션이 아직 시작하지 않았습니다."),


    //USER


    // HTTP STATUS CODE - 400
    ACCESS_TOKEN_INVALID("access token이 잘못되었습니다."),
    REFRESH_TOKEN_INVALID("refresh token이 잘못되었습니다."),
    VALIDATION_FAILED("유효성 검사 실패"),


    // HTTP STATUS CODE - 401
    UNAUTHORIZED("토큰이 존재하지 않습니다."),
    ACCESS_TOKEN_EXPIRED("access token이 만료되었습니다."),
    REFRESH_TOKEN_EXPIRED("refresh token이 만료되었습니다.");



    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
