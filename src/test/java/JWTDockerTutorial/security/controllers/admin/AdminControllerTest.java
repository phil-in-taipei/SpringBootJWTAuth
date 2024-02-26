package JWTDockerTutorial.security.controllers.admin;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.config.SecurityConfiguration;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.auth.AuthenticationService;
import JWTDockerTutorial.security.services.auth.JwtService;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(AdminController.class)
@ContextConfiguration(classes = {SecurityApplication.class})
@AutoConfigureMockMvc(addFilters = true)
@ActiveProfiles("test")
class AdminControllerTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    AuthenticationService authenticationService;

    @MockBean
    JwtService jwtService;

    @MockBean
    UserDetailsServiceImplementation userDetailsService;

    @MockBean
    UserRepository userRepository;

    User testUser = User.builder()
            .givenName("Test")
            .surname("User")
            .username("TestUser")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    User testUser2 = User.builder()
            .givenName("Test")
            .surname("User2")
            .username("TestUser2")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.USER)
            .build();

    User testAdmin = User.builder()
            .givenName("Test")
            .surname("Admin")
            .username("TestAdmin")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.ADMIN)
            .build();

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN",}, username = "TestAdmin")
    void authenticatedAdminInfo() throws Exception {
        when(userDetailsService.loadUserByUsername("TestAdmin"))
                .thenReturn(testAdmin);
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
    @WithMockUser(authorities = {"ROLE_ADMIN",}, username = "TestAdmin")
    void getStandardUsers() throws Exception {
        when(userDetailsService.getAllStandardUsers())
                .thenReturn(List.of(testUser, testUser2));
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
}