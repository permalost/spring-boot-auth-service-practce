package com.na.example.authservice.security;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthenticationManager implements ReactiveAuthenticationManager {

    @Autowired
    TokenUtil tokenUtil;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        String username;

        try {
            username = tokenUtil.getUsernameFromJWT(token);
        } catch (Exception ex) {
            username = null;
        }

        if (username != null && tokenUtil.validateToken(token)) {
            Claims claims = tokenUtil.getAllClaimsFromToken(token);
            List<String> roleMap = claims.get("roles", List.class);
            List<Role> roles = new ArrayList<>();
            for (String role : roleMap) {
                roles.add(Role.valueOf(role));
            }

            List<SimpleGrantedAuthority> authorities = roles.stream()
                                                            .map(authority -> new SimpleGrantedAuthority(authority.name()))
                                                            .collect(Collectors.toList());

            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(username, null, authorities);

            return Mono.just(auth);
        } else {
            return Mono.empty();
        }
    }
}
