package org.ein.erste.iot.account.services;

import lombok.RequiredArgsConstructor;
import org.ein.erste.iot.account.domain.Device;
import org.ein.erste.iot.account.domain.dto.DeviceDTO;
import org.ein.erste.iot.account.repositories.DeviceRepository;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(propagation = Propagation.REQUIRED)
public class DeviceCrudService implements CrudService<Device, DeviceDTO, UUID> {
    private final DeviceRepository deviceRepository;
    @Override
    public Device create(DeviceDTO dto) {
        return null;
    }

    @Override
    public Device read(UUID id) {
        return null;
    }

    @Override
    public DeviceDTO readDTO(UUID id) {
        return null;
    }

    @Override
    public Device update(UUID id, DeviceDTO dto) {
        return null;
    }

    @Override
    public void delete(UUID id) {

    }
}
