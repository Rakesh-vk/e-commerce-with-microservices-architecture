package com.ecommerce.OrderService.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class RestClientConfig {

    /**
     * Normal RestClient.Builder.
     *
     * This must remain unmodified so that infrastructure clients
     * such as Eureka can communicate with fixed URLs directly.
     */
    @Bean
    @Primary
    public RestClient.Builder restClientBuilder() {
        return RestClient.builder();
    }

    /**
     * Load-balanced RestClient.Builder.
     *
     * Used only for service-to-service calls through Eureka.
     */
    @Bean
    @LoadBalanced
    public RestClient.Builder loadBalancedRestClientBuilder() {
        return RestClient.builder();
    }

    /**
     * RestClient for ProductService.
     * Forwards the caller's Authorization header so downstream
     * services can authenticate the request.
     */
    @Bean
    public RestClient productServiceRestClient(
            @LoadBalanced RestClient.Builder builder) {

        return builder
                .baseUrl("http://PRODUCTSERVICE")
                .requestInterceptor((request, body, execution) -> {
                    forwardAuthHeader(request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }

    /**
     * RestClient for PaymentService.
     * Forwards the caller's Authorization header so downstream
     * services can authenticate the request.
     */
    @Bean
    public RestClient paymentServiceRestClient(
            @LoadBalanced RestClient.Builder builder) {

        return builder
                .baseUrl("http://PAYMENTSERVICE")
                .requestInterceptor((request, body, execution) -> {
                    forwardAuthHeader(request.getHeaders());
                    return execution.execute(request, body);
                })
                .build();
    }

    /**
     * Copies the Authorization header from the current inbound
     * request (the one OrderService is currently handling) onto
     * the outgoing request headers, so downstream service-to-service
     * calls stay authenticated as the original caller.
     */
    private void forwardAuthHeader(HttpHeaders outgoingHeaders) {
        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null) {
            return;
        }

        HttpServletRequest currentRequest = attrs.getRequest();
        String authHeader = currentRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader != null) {
            outgoingHeaders.set(HttpHeaders.AUTHORIZATION, authHeader);
        }
    }
}