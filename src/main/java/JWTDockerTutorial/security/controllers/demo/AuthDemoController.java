package JWTDockerTutorial.security.controllers.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpHeaders;


@RestController
@RequestMapping("/api/auth-required")
public class AuthDemoController {

    @GetMapping
    public ResponseEntity<String> confirmAuthenticated() {
        final HttpHeaders httpHeaders= new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new ResponseEntity<String>(
                "{\"message\": \"Response from authenticated endpoint successful\"}",
                httpHeaders, HttpStatus.OK
        );
    }
}
