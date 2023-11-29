package JWTDockerTutorial.security.repositories.user;
import java.util.Optional;

import JWTDockerTutorial.security.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
