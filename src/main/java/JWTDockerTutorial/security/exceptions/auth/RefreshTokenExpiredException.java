package JWTDockerTutorial.security.exceptions.auth;

import io.jsonwebtoken.ExpiredJwtException;

public class RefreshTokenExpiredException extends Exception {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }

    public RefreshTokenExpiredException() {}
}
