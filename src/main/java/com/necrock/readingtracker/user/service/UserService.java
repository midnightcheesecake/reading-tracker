package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.user.persistence.User;
import com.necrock.readingtracker.user.persistence.UserRepository;
import com.necrock.readingtracker.user.persistence.UserRole;
import com.necrock.readingtracker.user.persistence.UserStatus;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class UserService {

    private final UserRepository repository;
    private final Clock clock;

    public UserService(UserRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public User addUser(User user) {
        var enrichedUser =
                user.toBuilder().status(UserStatus.ACTIVE).role(UserRole.USER).createdAt(Instant.now(clock)).build();
        try {
            return repository.save(enrichedUser);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(
                    String.format("User with username '%s' already exists", user.getUsername()));
        }
    }

    public User updateUser(long id, User user) {
        var existingUser = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));

        var updatedUserBuilder = existingUser.toBuilder();
        if (user.getEmail() != null) {
            updatedUserBuilder.email(user.getEmail());
        }
        if (user.getPasswordHash() != null) {
            updatedUserBuilder.passwordHash(user.getPasswordHash());
        }

        return repository.save(updatedUserBuilder.build());
    }

    public void activateUser(long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
        var deletedUser = user.toBuilder().status(UserStatus.ACTIVE).build();
        repository.save(deletedUser);
    }

    public void deleteUser(long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
        var deletedUser = user.toBuilder().status(UserStatus.DELETED).build();
        repository.save(deletedUser);
    }

    public User getUser(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
    }

    public User findUserByUsername(String username) {
        return repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException(String.format("No user with username '%s'", username)));
    }

    public void assignUserRole(long id, UserRole newRole) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
        var updatedUser = user.toBuilder().role(newRole).build();
        repository.save(updatedUser);
    }

    public boolean hasUserRole(long id, UserRole requiredRole) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
        return user.getRole() == requiredRole;
    }
}
