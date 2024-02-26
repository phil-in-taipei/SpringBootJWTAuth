package JWTDockerTutorial.security.controllers.admin;

import JWTDockerTutorial.security.models.errors.ApiError;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    @Autowired
    UserDetailsServiceImplementation userService;
    /*
    @GetMapping("/test")
    public ResponseEntity<String> authenticatedAdmin() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(
                "{\"message\": \"Response from authenticated endpoint successful\"}",
                httpHeaders, HttpStatus.OK
        );
    }

     */

    @GetMapping("/authenticated")
    public ResponseEntity<Object> authenticatedAdminInfo(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        try {
            return ResponseEntity.ok(userService.loadUserByUsername(userDetails.getUsername()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        }
    }

    @GetMapping("/standard-users")
    public ResponseEntity<Object> getStandardUsers(Authentication authentication) {
        try {
            return ResponseEntity.ok(userService.getAllStandardUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiError(e.getMessage()));
        }
    }


}
