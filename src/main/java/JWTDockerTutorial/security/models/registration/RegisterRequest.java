package JWTDockerTutorial.security.models.registration;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

    private String givenName;

    private String surname;

    private String username;

    private String email;

    private String password;

    private String passwordConfirmation;


}
