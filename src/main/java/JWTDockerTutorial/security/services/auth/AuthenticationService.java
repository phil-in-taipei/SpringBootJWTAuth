package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.exceptions.auth.LoginFailureException;
import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.logging.BatchLogger;
import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;

    @Autowired
    JwtService jwtService;

    @Autowired
    UserDetailsServiceImplementation userService;

    @BatchLogger
    public AuthenticationResponse authenticate(
            AuthenticationRequest request
    ) throws UserNotFoundException, AuthenticationException, LoginFailureException {
        System.out.println(".......Running auth manager....");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (AuthenticationException e){
            throw new LoginFailureException("Login with the provided credentials failed. Please try again");
        }

        System.out.println(".......Getting user....");
        var user = userService.loadUserByUsername(request.getUsername());
        if (user == null) {
            throw new UserNotFoundException("The user does not exist!");
        }
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
    ) throws UserNotFoundException, RefreshTokenExpiredException, ExpiredJwtException {
        System.out.println(request);
        var jwtRefreshToken = request.getRefresh();
        System.out.println("This is the jwtToken in the auth refresh token class : " + jwtRefreshToken);
        try {
            var username = jwtService.extractUsername(jwtRefreshToken);
            var user = userService.loadUserByUsername(username.substring(0, username.length() / 2));
            var jwtToken = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .refresh(jwtRefreshToken)
                    .token(jwtToken)
                    .build();
        } catch (ExpiredJwtException e) {
            throw new RefreshTokenExpiredException(
                    "The login session has expired. Please login again");
        }
    }
}
