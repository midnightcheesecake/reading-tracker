package com.necrock.readingtracker.security.service;

import com.necrock.readingtracker.exception.handler.ApiError;
import com.necrock.readingtracker.exception.handler.ErrorResponseWriter;
import com.necrock.readingtracker.exception.handler.ErrorType;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final ErrorResponseWriter errorResponseWriter;

    public JwtAuthenticationFilter(JwtService jwtService, ErrorResponseWriter errorResponseWriter) {
        this.jwtService = jwtService;
        this.errorResponseWriter = errorResponseWriter;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            getJwtToken(request)
                    .map(jwtService::getToken)
                    .ifPresent(this::authenticateRequest);
            filterChain.doFilter(request, response);
        } catch (JwtException | UsernameNotFoundException e) {
            var error = new ApiError(ErrorType.UNAUTHORIZED_ERROR, "Invalid authentication token");
            errorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN, error);
        } catch (DisabledException ex) {
            var error = new ApiError(ErrorType.UNAUTHORIZED_ERROR, "User account is disabled");
            errorResponseWriter.write(response, HttpServletResponse.SC_FORBIDDEN, error);
        }
    }

    private Optional<String> getJwtToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Optional.empty();
        }

        return Optional.of(authHeader.substring(7));
    }

    private void authenticateRequest(JwtService.Token token) {
        if (token.getUsername() != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            token.validate();
            UserDetails userDetails = token.getUserDetails();

            var authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
    }
}
