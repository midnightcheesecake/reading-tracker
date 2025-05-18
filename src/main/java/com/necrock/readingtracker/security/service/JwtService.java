package com.necrock.readingtracker.security.service;

import com.necrock.readingtracker.exception.UnauthorizedException;
import com.necrock.readingtracker.user.service.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import static com.necrock.readingtracker.user.common.UserStatus.ACTIVE;

@Service
public class JwtService {

    private static final Duration EXPIRATION_TIME = Duration.ofHours(1);

    private final Key signingKey;
    private final Clock clock;
    private final CustomUserDetailsService userDetailsService;

    public JwtService(Key signingKey, Clock clock, CustomUserDetailsService userDetailsService) {
        this.signingKey = signingKey;
        this.clock = clock;
        this.userDetailsService = userDetailsService;
    }

    public String generateToken(User user) {
        var now = Instant.now(clock);
        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(EXPIRATION_TIME)))
                .signWith(signingKey)
                .compact();
    }

    public Token getToken(String tokenString) {
        return new Token(tokenString);
    }

    public class Token {
        private final Claims claims;

        private Token(String tokenString) {
            var parser = Jwts.parserBuilder()
                    .setClock(() -> Date.from(clock.instant()))
                    .setSigningKey(signingKey)
                    .build();
            claims = parser.parseClaimsJws(tokenString)
                    .getBody();
        }

        public String getUsername() {
            return claims.getSubject();
        }

        public UserDetails getUserDetails() {
            return userDetailsService.loadUserByUsername(getUsername());
        }

        public void validate() {
            var userDetails = getUserDetails();
            if (!userDetails.isEnabled()) {
                throw new DisabledException("User is disabled");
            }
        }
    }
}
