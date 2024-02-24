package JWTDockerTutorial.security.controllerEndpoints.registration;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.models.registration.RegisterRequest;
import JWTDockerTutorial.security.utils.TestUtil;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = SecurityApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class UserRegistrationControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

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

    @Test
    @Order(2)
    void registerUser() throws Exception {
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
    @Order(1)
    void registerUserPasswordConfirmationFailure() throws Exception {
        mockMvc.perform(post("/api/register/user")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(
                                testUserRegistrationRequestPasswordError)
                        )
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "The passwords do not match. Please try again.")
                );

    }

    @Test
    @Order(3)
    void registerUserDataIntegrityFailure() throws Exception {
        mockMvc.perform(post("/api/register/user")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(
                                testUserRegistrationRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "A user with that username already exists. Please try again")
                );

    }

    @Test
    @Order(5)
    void registerAdmin() throws Exception {
        mockMvc.perform(post("/api/register/admin")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(
                                testAdminRegistrationRequest)
                        )
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

    @Test
    @Order(6)
    void registerAdminDataIntegrityFailure() throws Exception {
        mockMvc.perform(post("/api/register/admin")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(
                                testAdminRegistrationRequest)
                        )
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "A user with that username already exists. Please try again")
                );

    }

    @Test
    @Order(4)
    void registerAdminPasswordConfirmationFailure() throws Exception {
        mockMvc.perform(post("/api/register/admin")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(
                                testAdminRegistrationRequestPasswordError)
                        )
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message")
                        .value(
                                "The passwords do not match. Please try again.")
                );

    }
}
