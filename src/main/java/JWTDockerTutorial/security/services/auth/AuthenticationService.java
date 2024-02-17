package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.logging.BatchLogger;
import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserRepository userRepository;

    @BatchLogger
    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) throws UserNotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UserNotFoundException("The user does not exist"));
        var jwtToken = jwtService.generateToken(user);
        var jwtRefreshToken = jwtService.generateRefreshToken(user);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token(jwtToken)
                .build();
    }

    @BatchLogger
    public AuthenticationResponse authenticateRefreshToken(
            TokenRefreshRequest request
    ) throws UserNotFoundException, RefreshTokenExpiredException {
        //System.out.println(request);
        var jwtRefreshToken = request.getRefresh();
        //System.out.println("This is the jwtToken in the auth refresh token class : " + jwtRefreshToken);
        var username = jwtService.extractUsername(jwtRefreshToken);
        //System.out.println("This is the username in the auth service class refresh token method: " + username);
        var user = userRepository.findByUsername(username.substring(0, username.length() / 2))
                .orElseThrow(() -> new UserNotFoundException("The user does not exist"));
        if (!jwtService.isRefreshTokenValid(jwtRefreshToken, user)) {
            throw new RefreshTokenExpiredException(
                    "The login session has expired. Please login again");
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .refresh(jwtRefreshToken)
                .token(jwtToken)
                .build();
    }
}
