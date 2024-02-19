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
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.any;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= SecurityApplication.class)
@ActiveProfiles("test")
class AuthenticationServiceTest {

    //@MockBean
    //AuthenticationManager authenticationManager;

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
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    String testToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjYiLCJpYXQiOjE3MDgzMTUwNzQsImV4cCI6MTcwODMxNTY3NH0.PCa9QIA8TDpELTCxjowGbJ5vcwE16o-AqBVW4JWGloQ";

    String testToken2 = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjYiLCJpYXQiOjE3MDgzMjU0NjEsImV4cCI6MTcwODMyNjA2MX0.KCM5B-9HATquKVnrauViQC9z_mfDZvzwUasn1cAcYBs";
    String testRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjZUZXN0VXNlcjYiLCJpYXQiOjE3MDgzMTUwNzQsImV4cCI6MTcwODQwMTQ3NH0.3RU0-MLIZeKqw5O2roHyb6B5ylQDaQUp2UoY2HS9TqM";

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
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
        //when(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        //                anyString(),
        //                anyString()
        //        )).isAuthenticated()).thenReturn(true);
        when(jwtService.generateToken(testUser))
                .thenReturn(testToken);
        when(jwtService.generateRefreshToken(testUser))
                .thenReturn(testRefreshToken);
        AuthenticationResponse testResponse = authenticationService.authenticate(testAuthRequest);
        System.out.println(testResponse);
        assertThat(testResponse.getToken())
                .isEqualTo(testAuthResponse.getToken());
        assertThat(testResponse.getRefresh())
                .isEqualTo(testAuthResponse.getRefresh());
    }

    @Test
    void authenticateRefreshToken() throws UserNotFoundException, RefreshTokenExpiredException {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
        when(jwtService.isRefreshTokenValid(testRefreshToken, testUser))
                .thenReturn(true);
        when(jwtService.extractUsername(testRefreshToken))
                .thenReturn(testUser.getUsername() + testUser.getUsername());
        when(jwtService.generateToken(testUser))
                .thenReturn(testToken2);
        AuthenticationResponse testResponse = authenticationService
                .authenticateRefreshToken(
                    testRefreshTokenRequest
        );
        System.out.println(testResponse);
        assertThat(testResponse.getToken())
                .isEqualTo(testToken2);
        assertThat(testResponse.getRefresh())
                .isEqualTo(testRefreshToken);
    }
}