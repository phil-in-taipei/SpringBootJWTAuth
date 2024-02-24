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

    // note: this controller is in another class "demo:AuthDemoController"
    @Test
    @Order(3)
    public void testConfirmExpiredFailure() throws Exception {
        String testTokenExp = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlciIsImlhdCI6MTcwODMzOTkyNSwiZXhwIjoxNzA4MzQwNTI1fQ.KatZsTHYGSrT5_dUX-d1aJ0LG5C1WeTIBQ71-CgisUo";
        String tokenString = "Bearer " + testTokenExp;
        mockMvc.perform(get("/api/auth-required")
                        .contentType("application/json")
                        .header("Authorization", tokenString)
                )
                //.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(
                        jsonPath("message")
                                .value(
                                        "Session Expired. Please login again"
                                )
                );
    }

    // note: this controller is in another class "demo:AuthDemoController"
    /*
    @Test
    @Order(4)
    public void testConfirmNonExistentUserFailure() throws Exception {
        // Note: the token used below must be generated in the past 10 minutes with
        // a user that is no longer in the database
        String testNonExistentUserToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjYiLCJpYXQiOjE3MDg2NzQzMTQsImV4cCI6MTcwODY3NDkxNH0.m2dzdwzhTONVDolwLBhXTSB05tz6NaAshWiCloDoozo";
        String tokenString = "Bearer " + testNonExistentUserToken;
        mockMvc.perform(get("/api/auth-required")
                        .contentType("application/json")
                        .header("Authorization", tokenString)
                )
                //.andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().contentType("application/json"))
                .andExpect(
                        jsonPath("message")
                                .value(
                                        "Authorization error"
                                )
                );
    }

     */



    @Test
    @Order(5)
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

    @Test
    @Order(6)
    public void testRefreshNewTokenExpiredError() throws Exception {
        String testRefreshTokenExp = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlclRlc3RVc2VyIiwiaWF0IjoxNzA4MzM5OTI1LCJleHAiOjE3MDg0MjYzMjV9.SoSyPS5PODhjAOzyVYZ4pMFIIQ0eHPObCUk96-TkMMw";
        TokenRefreshRequest testRefreshTokenRequest = new TokenRefreshRequest(
                testRefreshTokenExp
        );
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "The login session has expired. Please login again"
                        )
                );
    }

    /*
    @Test
    @Order(7)
    public void testRefreshNewTokenNonExistentUserError() throws Exception {
        // Note: the token used below must be generated in the past 24 hours with
        // a user that is no longer in the database
        String getTestNonExistentUserRefreshToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJUZXN0VXNlcjZUZXN0VXNlcjYiLCJpYXQiOjE3MDg2NzQzMTQsImV4cCI6MTcwODc2MDcxNH0.inaR5fd3XQfKWkCIrVRaLbl0piAvm5pebFMJCuy5mHE";
        TokenRefreshRequest testRefreshTokenRequest = new TokenRefreshRequest(
                getTestNonExistentUserRefreshToken
        );
        mockMvc.perform(post("/api/auth/refresh")
                        .contentType("application/json")
                        .content(TestUtil.convertObjectToJsonBytes(testRefreshTokenRequest))
                )
                //.andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("message")
                        .value(
                                "The user does not exist!"
                        )
                );
    }

     */

}
