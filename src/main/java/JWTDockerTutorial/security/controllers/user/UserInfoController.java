package JWTDockerTutorial.security.controllers.user;

import JWTDockerTutorial.security.exceptions.auth.RefreshTokenExpiredException;
import JWTDockerTutorial.security.exceptions.user.UserNotFoundException;
import JWTDockerTutorial.security.models.errors.ApiError;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserInfoController {


    @Autowired
    UserDetailsServiceImplementation userService;

    @GetMapping("/authenticated")
    public ResponseEntity<Object> authenticatedUserInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(userService.loadUserByUsername(userDetails.getUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        }
    }
}
