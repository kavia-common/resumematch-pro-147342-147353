package com.example.backend.repo.noop;

import com.example.backend.model.User;
import com.example.backend.repo.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.FluentQuery.FetchableFluentQuery;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.function.Function;

/**
 * No-op user repository implementation for no-mongo profile.
 */
@Repository
@Profile("no-mongo")
public class NoOpUserRepository implements UserRepository {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public <S extends User> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(UUID.randomUUID().toString());
        }
        users.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public <S extends User> List<S> saveAll(Iterable<S> entities) {
        List<S> saved = new ArrayList<>();
        entities.forEach(e -> {
            save(e);
            saved.add(e);
        });
        return saved;
    }

    @Override
    public <S extends User> S insert(S entity) {
        return save(entity);
    }

    @Override
    public <S extends User> List<S> insert(Iterable<S> entities) {
        return saveAll(entities);
    }

    @Override
    public Optional<User> findById(String s) {
        return Optional.ofNullable(users.get(s));
    }

    @Override
    public boolean existsById(String s) {
        return users.containsKey(s);
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findAll(Sort sort) {
        return findAll(); // No sorting in no-op implementation
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        throw new UnsupportedOperationException("Pagination not supported in no-mongo profile");
    }

    @Override
    public long count() {
        return users.size();
    }

    @Override
    public void deleteById(String s) {
        users.remove(s);
    }

    @Override
    public void delete(User entity) {
        users.remove(entity.getId());
    }

    @Override
    public void deleteAll() {
        users.clear();
    }

    @Override
    public void deleteAllById(Iterable<? extends String> strings) {
        strings.forEach(users::remove);
    }

    @Override
    public void deleteAll(Iterable<? extends User> entities) {
        entities.forEach(e -> users.remove(e.getId()));
    }

    @Override
    public List<User> findAllById(Iterable<String> strings) {
        List<User> found = new ArrayList<>();
        strings.forEach(id -> findById(id).ifPresent(found::add));
        return found;
    }

    @Override
    public <S extends User> Optional<S> findOne(Example<S> example) {
        return Optional.empty(); // Example matching not supported in no-op implementation
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example) {
        return Collections.emptyList(); // Example matching not supported in no-op implementation
    }

    @Override
    public <S extends User> List<S> findAll(Example<S> example, Sort sort) {
        return Collections.emptyList(); // Example matching not supported in no-op implementation
    }

    @Override
    public <S extends User> Page<S> findAll(Example<S> example, Pageable pageable) {
        throw new UnsupportedOperationException("Example matching and pagination not supported in no-mongo profile");
    }

    @Override
    public <S extends User> long count(Example<S> example) {
        return 0; // Example matching not supported in no-op implementation
    }

    @Override
    public <S extends User> boolean exists(Example<S> example) {
        return false; // Example matching not supported in no-op implementation
    }

    @Override
    public <S extends User, R> R findBy(Example<S> example, Function<FetchableFluentQuery<S>, R> queryFunction) {
        return null; // Advanced querying not supported in no-op implementation
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(u -> u.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }
}
