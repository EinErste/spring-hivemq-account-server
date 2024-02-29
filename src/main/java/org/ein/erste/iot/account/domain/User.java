package org.ein.erste.iot.account.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.ein.erste.iot.account.utils.UserRole;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true)
    @Size(min = 1, max = 320)
    @Email
    @NotNull
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "created")
    private Date created;

    @Column(name = "name")
    @Size(max = 250)
    private String name;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    private UserRole role;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.REFRESH)
    @JoinTable(name = "user_account",
            joinColumns = {@JoinColumn(name = "user_id", referencedColumnName="id")},
            inverseJoinColumns = {@JoinColumn(name = "account_id", referencedColumnName="account_id")})
    @Builder.Default
    private Set<Account> accounts = new LinkedHashSet<>();

    @OneToMany(cascade = {CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true, fetch = FetchType.LAZY, mappedBy = "owner")
    @Builder.Default
    private Set<Account> accountsOwn = new LinkedHashSet<>();
}
