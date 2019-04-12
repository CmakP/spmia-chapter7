package com.thoughtmechanix.organization;

import com.thoughtmechanix.organization.utils.UserContextFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.servlet.Filter;

/**
 * The @EnableResourceServer annotation is used to tell your microservice it’s a  protected resource.
 * Once you set up the organization service as a protected resource, every time a call is made to the service,
 * the caller has to include the Authentication HTTP header containing an OAuth2 access token to the service.
 * Your protected resource then has to call back to the OAuth2 service to see if the token is valid.
 *
 * The @EnableResourceServer enforces a filter that intercepts all incoming calls to the service, checks to see if
 * there’s an OAuth2 access token present in the incoming call’s HTTP header, and then calls back to the callback URL
 * defined in the security.oauth2.resource.userInfoUri to see if the token is valid.
 *
 * Once it knows the token is valid, the @EnableResourceServer annotation also applies any access control rules over
 * who and what can access a service, which is done in ResourceServerConfiguration class.
 */
@SpringBootApplication
@EnableEurekaClient
@EnableCircuitBreaker
@EnableResourceServer
public class Application {

    //TODO: Is this used? Commenting out this Bean would have no impacts on logging
    @Bean
    public Filter userContextFilter() {
        UserContextFilter userContextFilter = new UserContextFilter();
        return userContextFilter;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
