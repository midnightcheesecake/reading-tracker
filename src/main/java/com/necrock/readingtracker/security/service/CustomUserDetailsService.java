package com.necrock.readingtracker.security.service;

import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.user.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserService service;

    public CustomUserDetailsService(UserService service) {
        this.service = service;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            return new CustomUserDetails(service.getUser(username));
        } catch (NotFoundException e) {
            throw new UsernameNotFoundException("User not found", e);
        }
    }
}
