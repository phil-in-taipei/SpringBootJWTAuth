package JWTDockerTutorial.security.services.registration;

import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.RegisterRequest;
import JWTDockerTutorial.security.models.auth.RegistrationResponse;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserRegistrationService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    public RegistrationResponse register(RegisterRequest request) {
        var user = User.builder()
                .givenName(request.getGivenName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        return RegistrationResponse.builder()
                .userCreated("Account successfully created for user: " + user.getUsername())
                .build();
    }

    public RegistrationResponse registerAdmin(RegisterRequest request) {
        var user = User.builder()
                .givenName(request.getGivenName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ADMIN)
                .build();
        userRepository.save(user);
        return RegistrationResponse.builder()
                .userCreated("Account successfully created for admin: " + user.getUsername())
                .build();
    }

}
