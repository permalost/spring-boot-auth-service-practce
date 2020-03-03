package com.na.example.authservice.config;

import com.na.example.authservice.repositories.SecurityContextRepository;
import com.na.example.authservice.security.AuthenticationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {

    @Autowired
    private AuthenticationManager manager;

    @Autowired
    private SecurityContextRepository securityRepository;

    @Bean
    public SecurityWebFilterChain webFilterChain(ServerHttpSecurity http) {
        return http.exceptionHandling()
                   .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                   .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                   .and()
                   .csrf().disable()
                   .formLogin().disable()
                   .httpBasic().disable()
                   .authenticationManager(manager)
                   .securityContextRepository(securityRepository)
                   .authorizeExchange()
                   .pathMatchers(HttpMethod.OPTIONS).permitAll()
                   .pathMatchers("/login", "/anyone").permitAll()
                   .anyExchange().authenticated()
                   .and()
                   .build();
    }
}
