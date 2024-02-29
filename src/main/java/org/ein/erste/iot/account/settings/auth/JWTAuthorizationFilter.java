package org.ein.erste.iot.account.settings.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.ArrayList;

import static org.ein.erste.iot.account.settings.auth.AuthConstants.HEADER_STRING;

@Slf4j
public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    private final String SECRET;

    public JWTAuthorizationFilter(AuthenticationManager authManager, String secret, String tokenPrefix) {
        super(authManager);
        this.SECRET = secret;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String token = req.getHeader(HEADER_STRING);
        if (token == null) {
            chain.doFilter(req, res);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(@NonNull String token) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(SECRET.getBytes()))
                    .build()
                    .verify(token);
            String user = jwt.getSubject();
            if (user != null)
                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
            return null;
        } catch (TokenExpiredException ex) {
            log.debug("Token expired");
        } catch (JWTVerificationException ex) {
            log.debug("Token verification exception: " + ex.getMessage());
        }
        return null;
    }
}
