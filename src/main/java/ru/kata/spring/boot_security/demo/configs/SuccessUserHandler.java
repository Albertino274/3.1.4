package ru.kata.spring.boot_security.demo.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collection;

@Component
public class SuccessUserHandler implements AuthenticationSuccessHandler {
    private static final String ADMIN_ROLE = "ROLE_ADMIN";
    private static final String USER_ROLE = "ROLE_USER";
    private static final String ADMIN_REDIRECT = "/admin";
    private static final String USER_REDIRECT = "/user";
    private static final String DEFAULT_REDIRECT = "/";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

        if (containsAuthority(authorities, ADMIN_ROLE)) {
            response.sendRedirect(ADMIN_REDIRECT);
        } else if (containsAuthority(authorities, USER_ROLE)) {
            response.sendRedirect(USER_REDIRECT);
        } else {
            response.sendRedirect(DEFAULT_REDIRECT);
        }
    }

    private boolean containsAuthority(Collection<? extends GrantedAuthority> authorities, String role) {
        return authorities.stream()
                .anyMatch(auth -> auth.getAuthority().equals(role));
    }
}