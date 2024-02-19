package JWTDockerTutorial.security.controllers.demo;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/demo-controller")
public class DemoController {

    @GetMapping
    public ResponseEntity<String> confirmAuthenticated() {

        return ResponseEntity.ok(
                "Response from authenticated endpoint successful"
        );
    }
}
