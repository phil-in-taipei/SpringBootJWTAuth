package JWTDockerTutorial.security.services.registration;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.exceptions.auth.PasswordConfirmationFailureException;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import JWTDockerTutorial.security.models.registration.RegisterRequest;
import JWTDockerTutorial.security.models.registration.RegistrationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes= SecurityApplication.class)
@ActiveProfiles("test")
class UserRegistrationServiceTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserRegistrationService userRegistrationService;

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

    // TODO: test message in endpoint test
    String passwordMismatchMessage =  "The passwords do not match. Please try again.";

    @Test
    void register() throws PasswordConfirmationFailureException {
        when(userRepository.save(any(User.class)))
                .thenReturn(testUser);
        assertThat(userRegistrationService.register(testUserRegistrationRequest))
                .isEqualTo(testUserRegistrationResponse);
    }

    @Test
    public void testRegisterPasswordFailureBehavior()
            throws PasswordConfirmationFailureException {
        assertThrows(PasswordConfirmationFailureException.class, () -> {
            userRegistrationService.register(testUserRegistrationRequestPasswordError);
        });
    }

    @Test
    void registerAdmin() throws PasswordConfirmationFailureException {
        when(userRepository.save(any(User.class)))
                .thenReturn(testAdmin);
        assertThat(userRegistrationService.registerAdmin(testAdminRegistrationRequest))
                .isEqualTo(testAdminRegistrationResponse);
    }

    @Test
    public void testRegisterAdminPasswordFailureBehavior()
            throws PasswordConfirmationFailureException {
        assertThrows(PasswordConfirmationFailureException.class, () -> {
            userRegistrationService.registerAdmin(testAdminRegistrationRequestPasswordError);
        });
    }
}