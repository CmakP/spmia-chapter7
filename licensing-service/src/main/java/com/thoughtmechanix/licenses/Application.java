package com.thoughtmechanix.licenses;

import com.thoughtmechanix.licenses.config.ServiceConfig;
import com.thoughtmechanix.licenses.utils.UserContextInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.List;

/**
 * The @EnableResourceServer annotation is used to tell your microservice it’s a protected resource. This annotation
 * also tells Spring Cloud and Spring Security that the service is a protected resource.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableResourceServer //@see notes for this annotation in com.thoughtmechanix.organization.Application
public class Application {

    @Autowired
    private ServiceConfig serviceConfig;
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    /**
     * Defining a custom RestTemplate bean that will use a ClientHttpRequestInterceptor. The ClientHttpRequestInterceptor
     * is a Spring class that allows you to hook in functionality to be executed before a REST-based call is made.
     * ----
     * NOTE: The @EnableResourceServer enforces a filter that intercepts all incoming calls to the service, checks to
     *       see if there’s an OAuth2 access token present in the incoming call’s HTTP header, and then calls back to
     *       the callback URL defined in the security.oauth2.resource.userInfoUri to see if the token is valid. Once it
     *       knows the token is valid, the @EnableResourceServer annotation also applies any access control rules over
     *       who and what can access a service.
     * ----
     */
    @LoadBalanced
    @Bean
    public RestTemplate getCustomRestTemplate() {
        RestTemplate template = new RestTemplate();
        List interceptors = template.getInterceptors();
        if (interceptors == null) {
            template.setInterceptors(Collections.singletonList(new UserContextInterceptor()));
        } else {
            interceptors.add(new UserContextInterceptor());
            template.setInterceptors(interceptors);
        }

        return template;
    }


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
