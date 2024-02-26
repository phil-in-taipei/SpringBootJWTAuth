package JWTDockerTutorial.security.repositories.user;
import java.util.List;
import java.util.Optional;

import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    void deleteByUsername(String username);

    List<User> findByRole(Role role);

    Optional<User> findByUsername(String username);
}
