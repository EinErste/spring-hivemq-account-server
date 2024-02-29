package org.ein.erste.iot.account.services;

import lombok.RequiredArgsConstructor;
import org.ein.erste.iot.account.domain.User;
import org.ein.erste.iot.account.domain.dto.UserDTO;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class UserCrudService implements CrudService<User, UserDTO, UUID> {
    private final UserRepository userRepository;
    @Override
    public User create(UserDTO dto) {
        return null;
    }

    @Override
    public User read(UUID id) {
        return null;
    }

    @Override
    public UserDTO readDTO(UUID id) {
        return UserDTO.of(read(id));
    }

    @Override
    public User update(UUID id, UserDTO dto) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
