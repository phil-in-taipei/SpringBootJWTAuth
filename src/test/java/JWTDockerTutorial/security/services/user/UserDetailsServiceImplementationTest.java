package JWTDockerTutorial.security.services.user;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(classes= SecurityApplication.class)
@ActiveProfiles("test")
class UserDetailsServiceImplementationTest {

    @MockBean
    UserRepository userRepository;

    @Autowired
    UserDetailsServiceImplementation userService;

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("Test User")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    @Test
    void loadUserByUsername() throws UsernameNotFoundException {
        when(userRepository.findByUsername(anyString()))
                .thenReturn(Optional.ofNullable(testUser));
        assertThat(userService.loadUserByUsername("Test User"))
                .isEqualTo(testUser);
    }

    @Test
    public void testLoadUserByUsernameFailureBehavior()
            throws UsernameNotFoundException {
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("Test User");
        });
    }
}