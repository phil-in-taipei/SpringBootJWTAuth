package JWTDockerTutorial.security.exceptions.user;

import JWTDockerTutorial.security.models.user.User;

public class UserNotFoundException extends Exception {
    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException() {}
}
