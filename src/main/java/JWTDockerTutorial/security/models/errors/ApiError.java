package JWTDockerTutorial.security.models.errors;

public class ApiError {
    private String message;


    public ApiError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        this.message = message;
    }

}
