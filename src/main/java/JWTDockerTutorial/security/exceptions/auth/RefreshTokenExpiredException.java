package JWTDockerTutorial.security.exceptions.auth;

public class RefreshTokenExpiredException extends Exception {
    public RefreshTokenExpiredException(String message) {
        super(message);
    }

    public RefreshTokenExpiredException() {}
}
