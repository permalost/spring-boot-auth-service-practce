package com.na.example.authservice.controllers;

import com.na.example.authservice.security.TokenUtil;
import com.na.example.authservice.services.PrincipalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;


@WebFluxTest(controllers = Controller.class)
@Import({TokenUtil.class, PrincipalService.class})
class ControllerTest {

    @Autowired
    private WebTestClient client;

    @Test
    void testGetAnyone() {
        client.get().uri("/anyone").exchange().expectStatus().isOk();
    }

}