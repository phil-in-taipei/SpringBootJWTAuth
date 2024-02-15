package JWTDockerTutorial.security.controllers.auth;

import JWTDockerTutorial.security.models.auth.*;
import JWTDockerTutorial.security.services.auth.AuthenticationService;
import JWTDockerTutorial.security.services.registration.UserRegistrationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import JWTDockerTutorial.security.models.auth.AuthenticationResponse;


@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    @Autowired
    AuthenticationService authenticationService;
    //@Autowired
    //UserRegistrationService userRegistrationService;


    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refreshNewToken(
            @RequestBody TokenRefreshRequest request
    ) {
        System.out.println("This is the request data");
        return ResponseEntity.ok(authenticationService.authenticateRefreshToken(request));
    }

    //@PostMapping("/register")
    //public ResponseEntity<RegistrationResponse> register(
    //        @RequestBody RegisterRequest request
    //) {
    //    return ResponseEntity.ok(userRegistrationService.register(request));
    //}

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody AuthenticationRequest request
    ) {
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

}
