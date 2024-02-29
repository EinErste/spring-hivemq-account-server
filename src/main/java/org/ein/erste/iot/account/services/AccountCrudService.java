package org.ein.erste.iot.account.services;

import lombok.RequiredArgsConstructor;
import org.ein.erste.iot.account.domain.Account;
import org.ein.erste.iot.account.domain.dto.AccountDTO;
import org.ein.erste.iot.account.repositories.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class AccountCrudService implements CrudService<Account, AccountDTO, UUID> {
    private final AccountRepository accountRepository;
    @Override
    public Account create(AccountDTO dto) {
        return null;
    }

    @Override
    public Account read(UUID id) {
        return null;
    }

    @Override
    public AccountDTO readDTO(UUID id) {
        return null;
    }

    @Override
    public Account update(UUID id, AccountDTO dto) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
