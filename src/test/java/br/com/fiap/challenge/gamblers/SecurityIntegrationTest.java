package br.com.fiap.challenge.gamblers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class SecurityIntegrationTest {

    @LocalServerPort
    private int port;

    private final TestRestTemplate rest = new TestRestTemplate();

    @Test
    void healthEndpointAccessible() {
        ResponseEntity<String> res = rest.getForEntity("http://localhost:" + port + "/actuator/health", String.class);
        assertThat(res.getStatusCode()).isEqualTo(HttpStatus.OK);
    }    @Test
    void loginRequiresValidCredentials() {
        // try to login with invalid credentials and expect 401 or 400
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json");
        String body = "{\"email\":\"invalid@example.com\",\"password\":\"wrong\"}";
        ResponseEntity<String> res = rest.postForEntity("http://localhost:" + port + "/api/auth/login", new HttpEntity<>(body, headers), String.class);
        
        // Debug output to see what we're actually getting
        System.out.println("Response status: " + res.getStatusCode().value());
        System.out.println("Response body: " + res.getBody());
        
        // Accept 401 (Unauthorized) as the correct response for invalid credentials
        // Also accept 400 (Bad Request) in case of validation errors
        assertThat(res.getStatusCode().value()).isIn(400, 401);
    }
}
