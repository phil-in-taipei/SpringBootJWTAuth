package JWTDockerTutorial.security.controllerEndpoints.demo;

import JWTDockerTutorial.security.SecurityApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Order;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = SecurityApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class UserInfoControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    @WithUserDetails("TestUser")
    void authenticatedUserInfo() throws Exception {
        mockMvc.perform(get("/api/user/authenticated")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("givenName")
                        .value(
                                "Test"
                        )
                )
                .andExpect(jsonPath("surname")
                        .value(
                                "User"
                        )
                )
                .andExpect(jsonPath("email")
                        .value(
                                "test@gmx.com"
                        )
                )
                .andExpect(jsonPath("role")
                        .value(
                                "USER"
                        )
                )
                .andExpect(jsonPath("username")
                        .value(
                                "TestUser"
                        )
                );
    }
}
