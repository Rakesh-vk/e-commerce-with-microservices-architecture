package com.ecommerce.ApiGateway;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApiGatewayApplicationSmokeTest {

    @Test
    void applicationClass_shouldExist() {
        assertNotNull(ApiGatewayApplication.class);
    }
}
