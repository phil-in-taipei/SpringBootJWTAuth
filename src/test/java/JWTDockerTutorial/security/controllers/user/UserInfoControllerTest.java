package JWTDockerTutorial.security.controllers.user;

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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import org.springframework.security.core.Authentication;

@WebMvcTest(UserInfoController.class)
@ContextConfiguration(classes = {SecurityApplication.class})
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class UserInfoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    Authentication authentication;

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


    @Test
    @WithMockUser(authorities = {"USER",}, username = "TestUser")
    //@WithUserDetails("TestUser")
    void authenticatedUserInfo() throws Exception {
        when(authentication.getPrincipal()).thenReturn(testUser);
        when(userDetailsService.loadUserByUsername("TestUser"))
                .thenReturn(testUser);
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