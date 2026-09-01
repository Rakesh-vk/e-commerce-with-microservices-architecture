package com.ecommerce.OrderService.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.client.RestClient;

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
     */
    @Bean
    public RestClient productServiceRestClient(
            @LoadBalanced RestClient.Builder builder) {

        return builder
                .baseUrl("http://PRODUCTSERVICE")
                .build();
    }

    /**
     * RestClient for PaymentService.
     */
    @Bean
    public RestClient paymentServiceRestClient(
            @LoadBalanced RestClient.Builder builder) {

        return builder
                .baseUrl("http://PAYMENTSERVICE")
                .build();
    }
}