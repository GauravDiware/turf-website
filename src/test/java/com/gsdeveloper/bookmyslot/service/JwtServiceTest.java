package com.gsdeveloper.bookmyslot.service;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    @Test
    void shouldGenerateAndValidateToken() {
        JwtService jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "my-super-secret-key-for-bookmyslot-123456");
        ReflectionTestUtils.setField(jwtService, "jwtExpirationMs", 3_600_000L);

        UserDetails userDetails = org.springframework.security.core.userdetails.User.withUsername("client@example.com")
                .password("secret")
                .roles("CLIENT")
                .build();

        String token = jwtService.generateToken(userDetails);

        assertThat(token).isNotBlank();
        assertThat(jwtService.extractUsername(token)).isEqualTo("client@example.com");
        assertThat(jwtService.isTokenValid(token, userDetails)).isTrue();
    }
}
