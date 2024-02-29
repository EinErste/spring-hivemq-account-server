package org.ein.erste.iot.account.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "device")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {
    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "serial_number", unique = true)
    private String serialNumber;

    @ManyToOne(cascade = {CascadeType.REFRESH})
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(name = "mqtt_login", unique = true)
    private String mqttLogin;

    @Column(name = "mqtt_password", unique = true)
    private String mqttPassword;

    @Column(name = "mqtt_credentials_last_rotation")
    private Date mqttCredentialsLastRotation;

    @Column(name = "certificate")
    private byte[] certificate;

    @Column(name = "certificate_expiration")
    private Date certificateExpiration;

    @ElementCollection
    @CollectionTable(name = "device_topics", joinColumns = @JoinColumn(name = "device_id"))
    @Column(name = "topic")
    @Builder.Default
    private Set<String> topics = new HashSet<>();

}
