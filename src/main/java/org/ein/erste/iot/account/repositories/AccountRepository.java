package org.ein.erste.iot.account.repositories;

import org.ein.erste.iot.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccountRepository extends JpaRepository<Account, UUID> {
}