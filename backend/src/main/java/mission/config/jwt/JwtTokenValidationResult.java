package mission.config.jwt;

public enum JwtTokenValidationResult {
    VALID,
    EXPIRED,
    INVALID,
    REFRESH_TOKEN_DB_MISMATCH
}
