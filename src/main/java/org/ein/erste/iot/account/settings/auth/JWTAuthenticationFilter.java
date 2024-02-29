package org.ein.erste.iot.account.settings.auth;

import com.auth0.jwt.JWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.ein.erste.iot.account.domain.dto.UserDTO;
import org.ein.erste.iot.account.repositories.UserRepository;
import org.ein.erste.iot.account.utils.Pair;
import org.ein.erste.iot.account.utils.Response;
import org.ein.erste.iot.account.utils.errors.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import static com.auth0.jwt.algorithms.Algorithm.HMAC512;
import static org.ein.erste.iot.account.settings.auth.AuthConstants.*;
import static org.ein.erste.iot.account.utils.Utils.writeObjectToResponse;

@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final EntityManagerFactory entityManagerFactory;
    private final UserRepository userRepository;

    @Value("${jwt.access.token.expiration.time}")
    private int EXPIRATION_ACCESS_TIME;
    @Value("${jwt.access.token.secret}")
    private String SECRET_ACCESS;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            Pair<String, String> credentials = getAuthCredentials(req);
            if (credentials.getFirst() == null || credentials.getSecond() == null) {
                log.debug("Security exception: empty credentials");
                throw new BadCredentialsException("Wrong request");
            }
            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            credentials.getFirst().toLowerCase().trim(),
                            credentials.getSecond().trim(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            log.debug("Security exception: wrong credentials");
            throw new BadCredentialsException("Wrong request");
        }
    }

    private Pair<String, String> getAuthCredentials(HttpServletRequest req) throws IOException {
        String login;
        String password;
        if (req.getParameter(LOGIN_FIELD) != null && req.getParameter(PASSWORD_FIELD) != null) {
            login = req.getParameter(LOGIN_FIELD);
            password = req.getParameter(PASSWORD_FIELD);
        } else {
            UserDTO credentials = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            login = credentials.getLogin();
            password = credentials.getPassword();
        }
        return new Pair<>(login, password);
    }

    @Override
    @SneakyThrows
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException {
        EntityManager em = entityManagerFactory.createEntityManager();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        var userEntity = userRepository.findFirstByEmailEqualsIgnoreCase(((User) auth.getPrincipal()).getUsername())
                .orElseThrow(() -> {
                    em.close();
                    throw new NotFoundException("error.user.not.found");
                });

        userEntity = em.find(org.ein.erste.iot.account.domain.User.class, userEntity.getId());

        String email = userEntity.getEmail();
        Date expireAccess = new Date(System.currentTimeMillis() + EXPIRATION_ACCESS_TIME);
        String accessToken = JWT.create()
                .withSubject(userEntity.getId().toString())
                .withExpiresAt(expireAccess)
                .withClaim(USER_ROLE, userEntity.getRole().toString())
                .sign(HMAC512(SECRET_ACCESS.getBytes()));
        res.addHeader(HEADER_STRING, accessToken);
        res.addHeader(EXPIRATION_HEADER, String.valueOf(expireAccess.getTime()));
        writeObjectToResponse(Response.of(new AuthResponse(accessToken, expireAccess.getTime())), res);

        em.merge(userEntity);
        tx.commit();
        em.close();
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        SecurityContextHolder.clearContext();

        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null)
            ipAddress = request.getRemoteAddr();

        writeObjectToResponse(Response.of(HttpServletResponse.SC_BAD_REQUEST, "error.bad.request"), response);
    }

    record AuthResponse(String token, Long expiration){}
}
