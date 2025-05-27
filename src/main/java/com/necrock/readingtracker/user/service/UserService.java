package com.necrock.readingtracker.user.service;

import com.necrock.readingtracker.exception.NotFoundException;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import com.necrock.readingtracker.user.persistence.SafeUserRepository;
import com.necrock.readingtracker.user.common.UserRole;
import com.necrock.readingtracker.user.common.UserStatus;
import com.necrock.readingtracker.user.service.model.User;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Instant;

@Service
public class UserService {

    private final SafeUserRepository repository;
    private final UserEntityMapper mapper;
    private final Clock clock;

    public UserService(SafeUserRepository repository, UserEntityMapper mapper, Clock clock) {
        this.repository = repository;
        this.mapper = mapper;
        this.clock = clock;
    }

    public User addUser(User user) {
        validateAddPreconditions(user);
        var enrichedUser =
                user.toBuilder().status(UserStatus.ACTIVE).role(UserRole.USER).createdAt(Instant.now(clock)).build();
        return saveUser(enrichedUser);
    }

    private void validateAddPreconditions(User user) {
        // Check if no other user with username exists
        if (repository.findByUsername(user.getUsername()).isPresent()) {
            throw new AlreadyExistsException(
                    String.format("User with username '%s' already exists", user.getUsername()));
        }
    }

    public User updateUser(long id, User user) {
        var existingUser = getUser(id);

        var updatedUserBuilder = existingUser.toBuilder();
        if (user.getEmail() != null) {
            updatedUserBuilder.email(user.getEmail());
        }

        return saveUser(updatedUserBuilder.build());
    }

    public void setPassword(long id, String passwordHash) {
        var user = getUser(id);
        var updatedUser = user.toBuilder().passwordHash(passwordHash).build();
        saveUser(updatedUser);
    }

    public void setUserStatus(long id, UserStatus newStatus) {
        var user = getUser(id);
        var updatedUser = user.toBuilder().status(newStatus).build();
        saveUser(updatedUser);
    }

    public void setUserRole(long id, UserRole newRole) {
        var user = getUser(id);
        var updatedUser = user.toBuilder().role(newRole).build();
        saveUser(updatedUser);
    }

    public boolean hasUserRole(long id, UserRole requiredRole) {
        return getUser(id).getRole() == requiredRole;
    }

    public User getUser(long id) {
        return repository.findById(id)
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(String.format("No user with id %d", id)));
    }

    public User getUser(String username) {
        return repository.findByUsername(username)
                .map(mapper::toDomainModel)
                .orElseThrow(() -> new NotFoundException(String.format("No user with username '%s'", username)));
    }

    private User saveUser(User user) {
        return mapper.toDomainModel(repository.save(mapper.toEntity(user)));
    }
}
