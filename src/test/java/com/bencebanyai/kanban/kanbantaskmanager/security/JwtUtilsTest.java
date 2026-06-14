package com.bencebanyai.kanban.kanbantaskmanager.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {

    private JwtUtils jwtUtils;
    private final String testEmail = "test@example.com";

    @BeforeEach
    void setUp() {
        jwtUtils = new JwtUtils();
        ReflectionTestUtils.setField(jwtUtils, "jwtSecret", "ThisIsAVerySecureSecretKeyThatIsAtLeast32BytesLong=");
        ReflectionTestUtils.setField(jwtUtils, "jwtExpirationMs", 3600000L); // 1 hour
    }

    @Test
    void generateToken_ShouldReturnValidJwt() {
        String token = jwtUtils.generateToken(testEmail);

        assertNotNull(token);
        assertTrue(token.split("\\.").length == 3); // A JWT always has 3 parts separated by dots
    }

    @Test
    void extractEmail_ShouldReturnCorrectEmail() {
        String token = jwtUtils.generateToken(testEmail);
        String extractedEmail = jwtUtils.extractEmail(token);

        assertEquals(testEmail, extractedEmail);
    }

    @Test
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String token = jwtUtils.generateToken(testEmail);

        assertTrue(jwtUtils.validateToken(token));
    }

    @Test
    void validateToken_WithInvalidToken_ShouldReturnFalse() {
        String invalidToken = "eyJIUzI1NiJ9.InvalidPayload.FakeSignature";

        assertFalse(jwtUtils.validateToken(invalidToken));
    }
}