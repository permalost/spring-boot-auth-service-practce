package com.na.example.authservice.services;

import com.na.example.authservice.security.Role;
import com.na.example.authservice.security.UserPrincipal;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.Collections;

@Service
public class PrincipalService {

    private final UserPrincipal user = new UserPrincipal("user",
                                                         "user",
                                                         "user@example.com",
                                                         "example",
                                                         Arrays.asList(Role.ROLE_USER),
                                                         Arrays.asList(1,2,3));
    private final UserPrincipal admin = new UserPrincipal("admin",
                                                          "admin",
                                                          "admin@example.com",
                                                          "na",
                                                          Arrays.asList(Role.ROLE_USER, Role.ROLE_EMPLOYEE),
                                                          Collections.emptyList());


    public Mono<UserPrincipal> findByUsername(String username) {
        switch (username) {
            case "user":
                return Mono.just(user);
            case "admin":
                return Mono.just(admin);
            default:
                return Mono.empty();
        }
    }
}
