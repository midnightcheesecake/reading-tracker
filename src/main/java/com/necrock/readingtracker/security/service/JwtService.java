package com.necrock.readingtracker.security.service;

import com.necrock.readingtracker.user.persistence.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    private static final Duration EXPIRATION_TIME = Duration.ofHours(1);

    private final Key signingKey;
    private final Clock clock;

    public JwtService(Key signingKey, Clock clock) {
        this.signingKey = signingKey;
        this.clock = clock;
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
            claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(tokenString)
                    .getBody();
        }

        public String getUsername() {
            return claims.getSubject();
        }

        public boolean isValidFor(UserDetails userDetails) {
            return isForUser(userDetails) && !isExpired();
        }

        private boolean isForUser(UserDetails userDetails) {
            return claims.getSubject().equals(userDetails.getUsername());
        }

        private boolean isExpired() {
            return claims.getExpiration().before(Date.from(Instant.now(clock)));
        }
    }
}
