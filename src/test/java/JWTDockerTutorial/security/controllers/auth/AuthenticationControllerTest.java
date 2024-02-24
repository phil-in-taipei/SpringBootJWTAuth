package JWTDockerTutorial.security.controllers.auth;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.exceptions.auth.LoginFailureException;
import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;
import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.auth.AuthenticationService;
import JWTDockerTutorial.security.services.auth.JwtService;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import JWTDockerTutorial.security.utils.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AuthenticationController.class)
@ContextConfiguration(classes = {SecurityApplication.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class AuthenticationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserDetailsServiceImplementation userDetailsService;

    @MockBean
    UserRepository userRepository;

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

    AuthenticationResponse testRefreshTokenResponse = new AuthenticationResponse(
            testRefreshToken,
            testToken2
    );

    @Test
    void refreshNewToken() throws Exception {
        when(authenticationService.authenticateRefreshToken(testRefreshTokenRequest))
                .thenReturn(testRefreshTokenResponse);
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("refresh").value(testRefreshToken))
                .andExpect(jsonPath("token").value(testToken2));
    }

    @Test
    public void testRefreshNewTokenExpiredError() throws Exception {
        when(authenticationService.authenticateRefreshToken(testRefreshTokenRequest))
                .thenThrow(
                        new RefreshTokenExpiredException(
                                "The login session has expired. Please login again"
                        )
                );
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "The login session has expired. Please login again"
                        )
                );
    }

    @Test
    public void testRefreshNewTokenNonExistentUserError() throws Exception {
        when(authenticationService.authenticateRefreshToken(testRefreshTokenRequest))
                .thenThrow(
                        new UserNotFoundException(
                                "The user does not exist!"
                        )
                );
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "The user does not exist!"
                        )
                );
    }

    @Test
    void userLogin() throws Exception {
        when(authenticationService.authenticate(testAuthRequest))
                .thenReturn(testAuthResponse);
        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(this.testAuthRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("refresh").value(testRefreshToken))
                .andExpect(jsonPath("token").value(testToken));
    }

    @Test
    void userLoginNonExistentUserFailure() throws Exception {
        when(authenticationService.authenticate(testAuthRequest))
                .thenThrow(
                        new UserNotFoundException(
                                "The user does not exist!"
                        )
                );
        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(this.testAuthRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "The user does not exist!"
                        )
                );
    }

    @Test
    void userLoginCredentialsFailure() throws Exception {
        when(authenticationService.authenticate(testAuthRequest))
                .thenThrow(
                        new LoginFailureException(
                                "Login with the provided credentials failed. Please try again"
                        )
                );
        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(this.testAuthRequest))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "Login with the provided credentials failed. Please try again"
                        )
                );
    }
}