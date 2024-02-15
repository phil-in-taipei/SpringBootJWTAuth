package JWTDockerTutorial.security;

import org.junit.jupiter.api.Test;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;

@SpringBootTest
@Profile("test")
class SecurityApplicationTests implements CommandLineRunner {

	@Test
	void contextLoads() {
	}

	@Override
	//@Transactional
	public void run(String... args) throws Exception {
		System.out.print("*****************Running Tests**********************");
	}

}
