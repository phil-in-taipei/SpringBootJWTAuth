package JWTDockerTutorial.security.exceptions.auth;

public class LoginFailureException extends Exception {
    public LoginFailureException(String message) {
        super(message);
    }

    public LoginFailureException() {}
}
