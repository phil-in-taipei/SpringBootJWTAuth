package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= SecurityApplication.class)
@ActiveProfiles("test")
class AuthenticationServiceTest {

    @MockBean
    AuthenticationManager authenticationManager;

    @Autowired
    AuthenticationService authenticationService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    UserDetailsServiceImplementation userService;


    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("TestUser")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsImlhdCI6MTcwODMzOTkyNSwiZXhwIjoxNzA4MzQwNTI1fQ.KatZsTHYGSrT5_dUX-d1aJ0LG5C1WeTIBQ71-CgisUo";

    String testToken2 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsImlhdCI6MTcwODM0MDEzNywiZXhwIjoxNzA4MzQwNzM3fQ.GS3YaAptM2B9Lbu1ihZuyfJNZmgM_W5FztqLt8ubgQA";

    String testRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlclRlc3RVc2VyIiwiaWF0IjoxNzA4MzM5OTI1LCJleHAiOjE3MDg0MjYzMjV9.SoSyPS5PODhjAOzyVYZ4pMFIIQ0eHPObCUk96-TkMMw";

    AuthenticationRequest testAuthRequest = new AuthenticationRequest(
            testUser.getUsername(), testUser.getPassword()
    );
    AuthenticationResponse testAuthResponse = new AuthenticationResponse(
            testRefreshToken,
            testToken
    );

    TokenRefreshRequest testRefreshTokenRequest = new TokenRefreshRequest(testRefreshToken);


    @Test
    void authenticate() throws UserNotFoundException {
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(testUser);
        //when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        //                anyString(),
        //                anyString()
        //        )).isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(testToken);
        when(jwtService.generateRefreshToken(testUser))
                .thenReturn(testRefreshToken);
        AuthenticationResponse testResponse = authenticationService.authenticate(testAuthRequest);
        assertThat(testResponse.getToken())
                .isEqualTo(testAuthResponse.getToken());
        assertThat(testResponse.getRefresh())
                .isEqualTo(testAuthResponse.getRefresh());
    }

    @Test
    void authenticateUserNotFoundFailure() throws UserNotFoundException{
        testAuthRequest.setUsername("incorrect");
        when(userService.loadUserByUsername(anyString()))
                .thenReturn(null);
        assertThrows(UserNotFoundException.class, () -> {
            authenticationService.authenticate(testAuthRequest);
        });
    }

    /*
    @Test
    void authenticateRefreshToken() throws
            UserNotFoundException, RefreshTokenExpiredException {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
       // when(jwtService.isRefreshTokenValid(testRefreshToken, testUser))
        //        .thenReturn(true);
        when(jwtService.extractUsername(testRefreshToken))
                .thenReturn(testUser.getUsername() + testUser.getUsername());
        when(jwtService.generateToken(testUser))
                .thenReturn(testToken2);
        AuthenticationResponse testResponse = authenticationService
                .authenticateRefreshToken(
                    testRefreshTokenRequest
        );
        assertThat(testResponse.getToken())
                .isEqualTo(testToken2);
        assertThat(testResponse.getRefresh())
                .isEqualTo(testRefreshToken);
    }

    @Test
    void authenticateRefreshTokenExpiredFailure() {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
       // when(jwtService.isRefreshTokenValid(testRefreshToken, testUser))
       //         .thenReturn(false);
        when(jwtService.extractUsername(testRefreshToken))
                .thenReturn(testUser.getUsername() + testUser.getUsername());
        assertThrows(RefreshTokenExpiredException.class, () -> {
            authenticationService
                    .authenticateRefreshToken(
                            testRefreshTokenRequest
                    );
        });
    }

    @Test
    void authenticateRefreshTokenUserNotFoundFailure() {
        testAuthRequest.setUsername("incorrect");
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> {
            authenticationService
                    .authenticateRefreshToken(
                            testRefreshTokenRequest
                    );
        });
    }

     */
}