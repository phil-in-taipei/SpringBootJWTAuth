package JWTDockerTutorial.security.services.auth;

import JWTDockerTutorial.security.SecurityApplication;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes= SecurityApplication.class)
@ActiveProfiles("test")
class JwtServiceTest {

    //@MockBean
    //AuthenticationManager authenticationManager;

    @Autowired
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

    // note: the two tokens below will both have been expired
    // they will be used to measure length of token Strings and expiration
    String testTokenExp = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsImlhdCI6MTcwODMzOTkyNSwiZXhwIjoxNzA4MzQwNTI1fQ.KatZsTHYGSrT5_dUX-d1aJ0LG5C1WeTIBQ71-CgisUo";

    String testRefreshTokenExp = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlclRlc3RVc2VyIiwiaWF0IjoxNzA4MzM5OTI1LCJleHAiOjE3MDg0MjYzMjV9.SoSyPS5PODhjAOzyVYZ4pMFIIQ0eHPObCUk96-TkMMw";

    @Test
    void extractUsername() {
        String testToken = jwtService.generateToken(testUser);
        assertThat(jwtService.extractUsername(testToken))
                .isEqualTo(testUser.getUsername());
    }

    @Test
    void generateToken() {
        String testToken = jwtService.generateToken(testUser);
        assertThat(testToken.length())
                .isEqualTo(testTokenExp.length());
    }

    @Test
    void generateRefreshToken() {
        String testRefreshToken = jwtService.generateRefreshToken(testUser);
        assertThat(testRefreshToken.length())
                .isEqualTo(testRefreshTokenExp.length());
    }

    /*
    @Test
    void isRefreshTokenValid() {
        String testRefreshToken = jwtService.generateRefreshToken(testUser);
        assertTrue(jwtService.isRefreshTokenValid(testRefreshToken, testUser));
        //assertFalse(jwtService.isRefreshTokenValid(testRefreshTokenExp, testUser));
    }

     */


    @Test
    void isTokenValid() {
        String testToken = jwtService.generateToken(testUser);
        assertTrue(jwtService.isTokenValid(testToken, testUser));
        assertFalse(jwtService.isTokenValid(testTokenExp, testUser));
    }
}