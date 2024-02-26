package JWTDockerTutorial.security.services.user;

import JWTDockerTutorial.security.logging.BatchLogger;
import JWTDockerTutorial.security.logging.Loggable;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    //@Loggable
    @BatchLogger
    @Override
    public User loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(
                        "User not found with username: "
                                + username)
        );
    }

    @Loggable
    public List<User> getAllStandardUsers() {
        return userRepository.findByRole(Role.USER);
    }
}
