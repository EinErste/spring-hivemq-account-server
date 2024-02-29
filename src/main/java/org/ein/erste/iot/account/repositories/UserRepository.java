package org.ein.erste.iot.account.repositories;

import org.ein.erste.iot.account.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findFirstByEmailEqualsIgnoreCase(String email);
}