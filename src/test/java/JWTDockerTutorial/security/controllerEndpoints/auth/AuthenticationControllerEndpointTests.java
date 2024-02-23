package JWTDockerTutorial.security.controllerEndpoints.auth;

import JWTDockerTutorial.security.models.auth.TokenRefreshRequest;
import JWTDockerTutorial.security.utils.TestUtil;
import JWTDockerTutorial.security.SecurityApplication;
import JWTDockerTutorial.security.models.auth.AuthenticationRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.junit.jupiter.api.MethodOrderer;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Map;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SecurityApplication.class)
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class AuthenticationControllerEndpointTests {

    @Autowired
    MockMvc mockMvc;

    private static final ThreadLocal<String> refreshHolder = new ThreadLocal<>();

    private static final ThreadLocal<String> tokenHolder = new ThreadLocal<>();

    @Test
    @Order(1)
    public void testUserLogin() throws Exception {
        AuthenticationRequest testAuthRequest = new AuthenticationRequest(
                "TestUser", "testpassword"
        );


        MvcResult result = mockMvc.perform(post("/api/auth/authenticate")
                                .contentType("application/json")
                                .content(TestUtil.convertObjectToJsonBytes(testAuthRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("refresh").isNotEmpty())
                .andExpect(jsonPath("token").isNotEmpty())
                .andReturn();
        ObjectMapper mapper = new ObjectMapper();
        String responseBody = result.getResponse().getContentAsString();
        Map responseMap = mapper.readValue(responseBody, Map.class);
        refreshHolder.set((String) responseMap.get("refresh"));
        tokenHolder.set((String) responseMap.get("token"));
    }

    // note: this controller is in another class "demo:AuthDemoController"
    @Test
    @Order(2)
    public void testConfirmAuthenticated() throws Exception {
        String tokenString = "Bearer " + tokenHolder.get();
        mockMvc.perform(get("/api/auth-required")
                        .contentType("application/json")
                        .header("Authorization", tokenString)
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(
                        jsonPath("message")
                                .value(
                                        "Response from authenticated endpoint successful"
                                )
                );
    }

    @Test
    @Order(3)
    public void testRefreshNewToken() throws Exception {
        String refresh = refreshHolder.get();
        TokenRefreshRequest testRefreshTokenRequest = new TokenRefreshRequest(
                refresh
        );
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                //.andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("refresh").value(refresh))
                .andExpect(jsonPath("token").isNotEmpty());
    }

}
