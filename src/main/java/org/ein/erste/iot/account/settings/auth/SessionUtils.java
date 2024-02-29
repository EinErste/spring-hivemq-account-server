package org.ein.erste.iot.account.settings.auth;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ein.erste.iot.account.domain.User;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.ein.erste.iot.account.utils.UserRole;
import org.ein.erste.iot.account.utils.errors.BasicException;
import org.ein.erste.iot.account.utils.errors.NotFoundException;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.ReentrantLock;

@Service("sessionUtils")
@Scope(value = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
@RequiredArgsConstructor
@Slf4j
public class SessionUtils {
    private final UserRepository repository;
    private final ReentrantLock lock = new ReentrantLock();
    private User user;

    public boolean isAuthorized() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    }

    public void authorized() {
        if (!isAuthorized()) {
            throw new BasicException(HttpServletResponse.SC_UNAUTHORIZED,"error.user.not.authorized");
        }
    }

    public User getCurrentUser() {
        authorized();
        if (this.user == null) {
            lock.lock();
            try {
                user = getUserFromDB();
            } finally {
                lock.unlock();
            }
        }
        return this.user;
    }

    private User getUserFromDB() {
        String currentUserID = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> db = repository.findById(UUID.fromString(currentUserID));
        if (db.isPresent())
            return db.get();
        log.error("We can't find user in DB after it was authorized. Id is: {}", currentUserID);
        throw new NotFoundException("error.user.not.found");
    }


    public boolean isAdmin() {
        return getCurrentUser().getRole() == UserRole.ADMIN;
    }

    public UUID getUserId() {
        if (user == null)
            return null;
        return user.getId();
    }

    public String getUserEmail() {
        if (user == null)
            return null;
        return user.getEmail();
    }

}
