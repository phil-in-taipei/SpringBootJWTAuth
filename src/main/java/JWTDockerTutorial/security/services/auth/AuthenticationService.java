package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.RegisterRequest;
import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    // TODO: 12/1/23 Change this to just a message and have user login to continue
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .givenName(request.getGivenName())
                .surname(request.getSurname())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticateRefreshToken(TokenRefreshRequest request) {
        System.out.println(request);
        var jwtRefreshToken = request.getRefresh();
        System.out.println("This is the jwtToken in the auth refresh token class : " + jwtRefreshToken);
        var username = jwtService.extractUsername(jwtRefreshToken);
        System.out.println("This is the username in the auth service class refresh token method: " + username);
        String[] doubleUsername = username.split("_");
        System.out.println(doubleUsername.length);
        System.out.println(doubleUsername[0]);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token("This will eventually be a token")
                .build();
    }
}
