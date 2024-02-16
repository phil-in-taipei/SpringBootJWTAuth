package JWTDockerTutorial.security.controllers.auth;

import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.models.auth.*;
import JWTDockerTutorial.security.services.auth.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import JWTDockerTutorial.security.models.errors.ApiError;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;

    @PostMapping("/refresh")
    public ResponseEntity<Object> refreshNewToken(
            @RequestBody TokenRefreshRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.authenticateRefreshToken(request));
        } catch (UserNotFoundException | RefreshTokenExpiredException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

    @PostMapping("/authenticate")
    public ResponseEntity<Object> register(
            @RequestBody AuthenticationRequest request
    ) {
        try {
            return ResponseEntity.ok(authenticationService.authenticate(request));
        } catch (UserNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(
                    new ApiError("There was an error. Please try again")
            );
        }
    }

}
