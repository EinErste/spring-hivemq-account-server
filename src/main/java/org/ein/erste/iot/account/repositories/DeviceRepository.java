package org.ein.erste.iot.account.repositories;

import org.ein.erste.iot.account.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeviceRepository extends JpaRepository<Device, UUID> {
}