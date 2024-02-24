package JWTDockerTutorial.security;

import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
//import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@Profile("test")
public class SecurityApplicationTest implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    //@Autowired
    //private UserDetailsServiceImplementation userService;

    public static void main(String[] args) {
        SpringApplication
                .run(SecurityApplication.class, args);
    }


    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println("----------------Preparing Database for Tests---------------");
        // note: these mock users differ from the ones below (spaces in username)
        // The mock users deleted here are created in the UserRegistrationControllerEndpointTest class
        userRepository.deleteByUsername("Test User");
        userRepository.deleteByUsername("Test Admin");
        if (userRepository.findAll().isEmpty()) {
            System.out.println("The user repo is empty. Creating mock users");
            User testUser = User.builder()
                    .givenName("Test")
                    .surname("User")
                    .username("TestUser")
                    .email("test@gmx.com")
                    .password(passwordEncoder.encode("testpassword"))
                    .role(Role.USER)
                    .build();
            userRepository.save(testUser);
            User testAdmin = User.builder()
                    .givenName("Test")
                    .surname("Admin")
                    .username("TestAdmin")
                    .email("test@gmx.com")
                    .password(passwordEncoder.encode("testpassword"))
                    .role(Role.ADMIN)
                    .build();
            userRepository.save(testAdmin);
        }
        System.out.println("*****************Running Tests**********************");
    }
}
