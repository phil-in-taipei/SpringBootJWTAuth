package JWTDockerTutorial.security.controllers.registration;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.exceptions.auth.PasswordConfirmationFailureException;
import JWTDockerTutorial.security.models.registration.RegisterRequest;
import JWTDockerTutorial.security.models.registration.RegistrationResponse;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.auth.JwtService;
import JWTDockerTutorial.security.services.registration.UserRegistrationService;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import JWTDockerTutorial.security.utils.TestUtil;

import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;

@WebMvcTest(UserRegistrationController.class)
@ContextConfiguration(classes = {SecurityApplication.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserRegistrationControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserDetailsServiceImplementation userDetailsService;

    @MockBean
    UserRegistrationService userRegistrationService;

    @MockBean
    UserRepository userRepository;

    User testAdmin = User.builder()
            .givenName("Test")
            .surname("Admin")
            .username("Test Admin")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.ADMIN)
            .build();

    RegisterRequest testAdminRegistrationRequest = RegisterRequest.builder()
            .givenName("Test")
            .surname("Admin")
            .username("Test Admin")
            .email("test@gmx.com")
            .password("testpassword")
            .passwordConfirmation("testpassword")
            .build();

    RegisterRequest testAdminRegistrationRequestPasswordError = RegisterRequest.builder()
            .givenName("Test")
            .surname("Admin")
            .username("Test Admin")
            .email("test@gmx.com")
            .password("testpassword")
            .passwordConfirmation("testpassword!!")
            .build();

    RegistrationResponse testAdminRegistrationResponse = RegistrationResponse
            .builder()
            .message("Account successfully created for admin: " +
                    testAdmin.getUsername())
            .build();

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    RegisterRequest testUserRegistrationRequest = RegisterRequest.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .passwordConfirmation("testpassword")
            .build();

    RegisterRequest testUserRegistrationRequestPasswordError = RegisterRequest.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .passwordConfirmation("testpassword!!")
            .build();

    RegistrationResponse testUserRegistrationResponse = RegistrationResponse
            .builder()
            .message("Account successfully created for user: " +
                    testUser.getUsername())
            .build();

    @Test
    void registerUser() throws Exception {
        when(userRegistrationService.register(testUserRegistrationRequest))
                .thenReturn(testUserRegistrationResponse);
        mockMvc.perform(post("/api/register/user")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testUserRegistrationRequest))
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Account successfully created for user: Test User")
                );

    }

    @Test
    void registerAdmin() throws Exception {
        when(userRegistrationService.registerAdmin(testAdminRegistrationRequest))
                .thenReturn(testAdminRegistrationResponse);
        mockMvc.perform(post("/api/register/admin")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testAdminRegistrationRequest))
                )
                //.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "Account successfully created for admin: Test Admin")
                );
    }
}