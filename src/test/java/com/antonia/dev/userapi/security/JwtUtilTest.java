package com.antonia.dev.userapi.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("JwtUtil Tests")
class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String testSecret;
    private Long testExpiration;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        testSecret = "mySecretKeyForTestingPurposesWithAtLeast256Bits123456789";
        testExpiration = 3600000L;

        ReflectionTestUtils.setField(jwtUtil, "secret", testSecret);
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should generate token successfully")
    void generateToken_WithValidData_ShouldReturnToken() {
        String username = "testuser";
        String role = "USER";

        String token = jwtUtil.generateToken(username, role);

        assertThat(token).isNotNull();
        assertThat(token).isNotEmpty();
    }

    @Test
    @DisplayName("Should extract username from token")
    void extractUsername_WithValidToken_ShouldReturnUsername() {
        String username = "testuser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);

        String extractedUsername = jwtUtil.extractUsername(token);

        assertThat(extractedUsername).isEqualTo(username);
    }

    @Test
    @DisplayName("Should extract role from token")
    void extractRole_WithValidToken_ShouldReturnRole() {
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtUtil.generateToken(username, role);

        String extractedRole = jwtUtil.extractRole(token);

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Should extract expiration date from token")
    void extractExpiration_WithValidToken_ShouldReturnExpirationDate() {
        // Given
        String username = "testuser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);

        // When
        Date expiration = jwtUtil.extractExpiration(token);

        // Then
        assertThat(expiration).isNotNull();
        assertThat(expiration).isAfter(new Date());
    }

    @Test
    @DisplayName("Should validate token with UserDetails successfully")
    void validateToken_WithValidTokenAndUserDetails_ShouldReturnTrue() {
        String username = "testuser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);
        
        UserDetails userDetails = new User(
                username,
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate token with wrong username")
    void validateToken_WithWrongUsername_ShouldReturnFalse() {
        String username = "testuser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);
        
        UserDetails userDetails = new User(
                "wronguser",
                "password",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Boolean isValid = jwtUtil.validateToken(token, userDetails);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should validate token without UserDetails successfully")
    void validateToken_WithValidToken_ShouldReturnTrue() {
        String username = "testuser";
        String role = "USER";
        String token = jwtUtil.generateToken(username, role);

        Boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("Should invalidate expired token")
    void validateToken_WithExpiredToken_ShouldReturnFalse() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", -1000L); // Token expirado
        String token = jwtUtil.generateToken("testuser", "USER");
        
        ReflectionTestUtils.setField(jwtUtil, "expiration", testExpiration);

        Boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should invalidate malformed token")
    void validateToken_WithMalformedToken_ShouldReturnFalse() {
        String malformedToken = "this.is.not.a.valid.jwt.token";

        Boolean isValid = jwtUtil.validateToken(malformedToken);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("Should extract custom claims from token")
    void extractClaim_WithValidToken_ShouldReturnClaim() {
        String username = "testuser";
        String role = "ADMIN";
        String token = jwtUtil.generateToken(username, role);

        String extractedRole = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));

        assertThat(extractedRole).isEqualTo(role);
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_ForDifferentUsers_ShouldReturnDifferentTokens() {
        String user1 = "user1";
        String user2 = "user2";
        String role = "USER";

        String token1 = jwtUtil.generateToken(user1, role);
        String token2 = jwtUtil.generateToken(user2, role);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should generate different tokens for different roles")
    void generateToken_ForDifferentRoles_ShouldReturnDifferentTokens() {
        String username = "testuser";
        String role1 = "USER";
        String role2 = "ADMIN";

        String token1 = jwtUtil.generateToken(username, role1);
        String token2 = jwtUtil.generateToken(username, role2);

        assertThat(token1).isNotEqualTo(token2);
    }

    @Test
    @DisplayName("Should token contain issued date close to now")
    void generateToken_ShouldContainCorrectIssuedDate() {
        String username = "testuser";
        String role = "USER";

        String token = jwtUtil.generateToken(username, role);
        
        // Extract issued date manually
        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Date issuedAt = claims.getIssuedAt();

        long timeDifference = Math.abs(System.currentTimeMillis() - issuedAt.getTime());
        assertThat(timeDifference).isLessThan(2000); // 2 seconds
    }

    @Test
    @DisplayName("Should token expiration be correct duration")
    void generateToken_ShouldHaveCorrectExpirationDuration() {
        String username = "testuser";
        String role = "USER";
        Date beforeGeneration = new Date();

        String token = jwtUtil.generateToken(username, role);
        Date expiration = jwtUtil.extractExpiration(token);

        long actualDuration = expiration.getTime() - beforeGeneration.getTime();
        assertThat(actualDuration).isCloseTo(testExpiration, org.assertj.core.data.Offset.offset(1000L));
    }
}
