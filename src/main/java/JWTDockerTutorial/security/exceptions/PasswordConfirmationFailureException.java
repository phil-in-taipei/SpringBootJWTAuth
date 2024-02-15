package JWTDockerTutorial.security.exceptions;

public class PasswordConfirmationFailureException extends Exception  {
    public PasswordConfirmationFailureException(String message) {
        super(message);
    }

    public PasswordConfirmationFailureException() {}
}
