package com.na.example.authservice.controllers;

import com.na.example.authservice.security.AuthRequest;
import com.na.example.authservice.security.AuthResponse;
import com.na.example.authservice.security.TokenUtil;
import com.na.example.authservice.services.PrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/")
public class Controller {

    @Autowired
    TokenUtil tokenUtil;

    @Autowired
    PrincipalService principalService;

    @PostMapping("/login")
    public Mono login(@RequestBody AuthRequest authRequest) {
        return principalService.findByUsername(authRequest.getUsername()).map(userDetails ->  {
            if (authRequest.getPassword().equals(userDetails.getPassword())) {
                return ResponseEntity.ok(new AuthResponse(tokenUtil.generateToken(userDetails)));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }).defaultIfEmpty(ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('EMPLOYEE')")
    Mono<String> admin() {
        return Mono.just("admin");
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    Mono<String> user() {
        return Mono.just("user");
    }

    @GetMapping("/account/{id}")
    @PreAuthorize("hasPermission(#id, 'read') and hasRole('USER')")
    Mono<Integer> account(@PathVariable int id) { return Mono.just(id); }

    @GetMapping("/authenticated")
    @PreAuthorize("isAuthenticated()")
    Mono<String> authenticated() {
        return Mono.just("authenticated");
    }

    @GetMapping("/anyone")
    @PreAuthorize("isAnonymous()")
    Mono<String> anyone() {
        return Mono.just("anyone");
    }
}
