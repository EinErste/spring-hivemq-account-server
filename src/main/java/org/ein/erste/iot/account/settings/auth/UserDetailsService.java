package org.ein.erste.iot.account.settings.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ein.erste.iot.account.domain.User;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.ein.erste.iot.account.utils.errors.BasicException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("customUserDetailsService")
@RequiredArgsConstructor
@Slf4j
public class UserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserRepository userRepository;
    private Map<String, String> hardcoded;

    private final PasswordEncoder passwordEncoder;

    @Value("${hardcoded.user.email.super.admin}")
    private String ADMINISTRATOR_EMAIL;
    @Value("${hardcoded.user.password.super.admin}")
    private String ADMINISTRATOR_PASSWORD;

    @Transactional(rollbackFor = BasicException.class)
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        User user = userRepository.findFirstByEmailEqualsIgnoreCase(login).orElseThrow(() -> new UsernameNotFoundException("Username not found"));
        String password;
        if (formHardcodedPermissions().containsKey(login))
            password = passwordEncoder.encode(formHardcodedPermissions().get(login));
        else
            password = user.getPasswordHash();
        return new org.springframework.security.core.userdetails.User(user.getEmail(), password,
                true, true, true, true, getGrantedAuthorities(user));
    }

    private List<GrantedAuthority> getGrantedAuthorities(User user) {
        return List.of();
    }

    private Map<String, String> formHardcodedPermissions() {
        if (hardcoded == null) {
            hardcoded = new HashMap<>();
            hardcoded.put(ADMINISTRATOR_EMAIL, ADMINISTRATOR_PASSWORD);
        }
        return hardcoded;
    }

}
