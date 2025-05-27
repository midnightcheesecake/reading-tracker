package com.necrock.readingtracker.user.persistence;

import com.google.common.collect.ImmutableMap;
import com.necrock.readingtracker.common.SafeRepository;
import com.necrock.readingtracker.exception.AlreadyExistsException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

@Component
public class SafeUserRepository extends SafeRepository<UserEntity, Long> {
    private final UserRepository repository;

    public SafeUserRepository(UserRepository repository) {
        super(repository);
        this.repository = repository;
    }

    @Override
    protected ImmutableMap<String, Function<UserEntity, RuntimeException>> getUniqueConstraints() {
        return ImmutableMap.of(
                UserEntity.UNIQUE_USERNAME,
                entity -> new AlreadyExistsException(
                        String.format(
                                "User with username '%s' already exists",
                                entity.getUsername()))
        );
    }

    public Optional<UserEntity> findByUsername(String username) {
        return repository.findByUsername(username);
    }
}
