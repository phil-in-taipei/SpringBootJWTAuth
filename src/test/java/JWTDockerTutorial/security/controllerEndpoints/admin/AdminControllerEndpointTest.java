package JWTDockerTutorial.security.controllerEndpoints.admin;

import JWTDockerTutorial.security.SecurityApplication;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest(classes = SecurityApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class AdminControllerEndpointTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    @Order(1)
    @WithUserDetails("TestAdmin")
    void authenticatedAdminInfo() throws Exception {
        mockMvc.perform(get("/api/admin/authenticated")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("givenName")
                        .value(
                                "Test"
                        )
                )
                .andExpect(jsonPath("surname")
                        .value(
                                "Admin"
                        )
                )
                .andExpect(jsonPath("email")
                        .value(
                                "test@gmx.com"
                        )
                )
                .andExpect(jsonPath("role")
                        .value(
                                "ADMIN"
                        )
                )
                .andExpect(jsonPath("username")
                        .value(
                                "TestAdmin"
                        )
                );
    }

    @Test
    @Order(2)
    @WithUserDetails("TestUser")
    void authenticatedAdminInfoPermissionsError() throws Exception {
        mockMvc.perform(get("/api/admin/authenticated")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "Authorization permissions error"
                        )
                );
    }

    @Test
    @Order(3)
    @WithUserDetails("TestAdmin")
    void getStandardUsers() throws Exception {
        mockMvc.perform(get("/api/admin/standard-users")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0]givenName")
                        .value(
                                "Test"
                        )
                )
                .andExpect(jsonPath("$[0]surname")
                        .value(
                                "User"
                        )
                )
                .andExpect(jsonPath("$[0]email")
                        .value(
                                "test@gmx.com"
                        )
                )
                .andExpect(jsonPath("$[0]role")
                        .value(
                                "USER"
                        )
                )
                .andExpect(jsonPath("$[0]username")
                        .value(
                                "TestUser"
                        )
                )
                .andExpect(jsonPath("$[1]givenName")
                        .value(
                                "Test"
                        )
                )
                .andExpect(jsonPath("$[1]surname")
                        .value(
                                "User2"
                        )
                )
                .andExpect(jsonPath("$[1]email")
                        .value(
                                "test@gmx.com"
                        )
                )
                .andExpect(jsonPath("$[1]role")
                        .value(
                                "USER"
                        )
                )
                .andExpect(jsonPath("$[1]username")
                        .value(
                                "TestUser2"
                        )
                );
    }

    @Test
    @Order(4)
    @WithUserDetails("TestUser")
    void getStandardUsersPermissionsError() throws Exception {
        mockMvc.perform(get("/api/admin/standard-users")
                        .contentType("application/json")
                )
                //.andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "Authorization permissions error"
                        )
                );
    }
}
