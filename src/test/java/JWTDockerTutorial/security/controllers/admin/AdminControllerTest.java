package JWTDockerTutorial.security.controllers.admin;

import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.models.user.Role;
import JWTDockerTutorial.security.models.user.User;
import JWTDockerTutorial.security.repositories.user.UserRepository;
import JWTDockerTutorial.security.services.auth.AuthenticationService;
import JWTDockerTutorial.security.services.auth.JwtService;
import JWTDockerTutorial.security.services.user.UserDetailsServiceImplementation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

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

    User testAdmin = User.builder()
            .givenName("Test")
            .surname("Admin")
            .username("TestAdmin")
            .email("test@gmx.com")
            .password("testpassword")
            .role(Role.ADMIN)
            .build();

    @Test
    @WithMockUser(authorities = {"ADMIN",}, username = "TestAdmin")
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
    @WithMockUser(authorities = {"USER",}, username = "TestUser")
    void authenticatedAdminPermissionsError() throws Exception {
        when(userDetailsService.loadUserByUsername("TestUser"))
                .thenReturn(testUser);
        mockMvc.perform(get("/api/admin/authenticated")
                        .contentType("application/json")
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "Authorization permissions error"
                        )
                );
    }

    @Test
    void getStandardUsers() {
    }
}