package JWTDockerTutorial.security.controllers.registration;

import JWTDockerTutorial.security.models.auth.RegisterRequest;
import JWTDockerTutorial.security.models.auth.RegistrationResponse;
import JWTDockerTutorial.security.services.registration.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class UserRegistrationController {

    @Autowired
    UserRegistrationService userRegistrationService;

    @PostMapping("/user")
    public ResponseEntity<RegistrationResponse> registerUser(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(userRegistrationService.register(request));
    }

    @PostMapping("/admin")
    public ResponseEntity<RegistrationResponse> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        return ResponseEntity.ok(userRegistrationService.registerAdmin(request));
    }
}
