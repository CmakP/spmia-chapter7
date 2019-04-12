package com.thoughtmechanix.licenses.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * The definition of what user roles have permissions to do what actions occurs at the individual service level.
 * Your protected resource then has to use the callback URL defined in the application.yml to call back to the
 * OAuth2 service to see if the token is valid.
 */
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(ResourceServerConfiguration.class);

    @Override
    public void configure(HttpSecurity http) throws Exception {

        logger.debug("### licenses.ResourceServerConfiguration - Configuring user role permissions at service level.");

            http
                    .authorizeRequests()
                    .antMatchers("/v1/organizations/**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated();
    }
}
