package JWTDockerTutorial.security.controllers.registration;

import JWTDockerTutorial.security.exceptions.PasswordConfirmationFailureException;
import JWTDockerTutorial.security.models.auth.RegisterRequest;
import JWTDockerTutorial.security.services.registration.UserRegistrationService;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/register")
@RequiredArgsConstructor
public class UserRegistrationController {

    @Autowired
    UserRegistrationService userRegistrationService;

    @PostMapping("/user")
    public ResponseEntity<?> registerUser(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(userRegistrationService.register(request));
        } catch (PasswordConfirmationFailureException  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("There was an error. Please try again");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("A user with that username already exists. Please try again");
        }
    }

    @PostMapping("/admin")
    public ResponseEntity<?> registerAdmin(
            @RequestBody RegisterRequest request
    ) {
        try {
            return ResponseEntity.ok(userRegistrationService.registerAdmin(request));

        } catch (PasswordConfirmationFailureException  e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body("There was an error. Please try again");
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.badRequest().body("A user with that username already exists. Please try again");
        }
    }
}
