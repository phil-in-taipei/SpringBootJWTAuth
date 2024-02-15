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


    // TODO: 12/1/2023 Change this to just a message and have user login to continue
    // TODO: 2/14/2024 implement separate method/endpoint for admin registration


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

    // TODO: 2/14/2024 error handling for expired refresh token with isRefreshTokenValid method
    // TODO: 2/14/2024 don't use underscore to divide doubled username for refresh
    public AuthenticationResponse authenticateRefreshToken(TokenRefreshRequest request) {
        System.out.println(request);
        var jwtRefreshToken = request.getRefresh();
        System.out.println("This is the jwtToken in the auth refresh token class : " + jwtRefreshToken);
        var username = jwtService.extractUsername(jwtRefreshToken);
        System.out.println("This is the username in the auth service class refresh token method: " + username);
        String[] doubleUsername = username.split("_");
        System.out.println(doubleUsername.length);
        System.out.println(doubleUsername[0]);
        var user = userRepository.findByUsername(doubleUsername[0])
                .orElseThrow();
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token(jwtToken)
                .build();
    }
}
