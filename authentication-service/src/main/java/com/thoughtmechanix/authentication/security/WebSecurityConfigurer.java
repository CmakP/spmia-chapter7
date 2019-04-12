package com.thoughtmechanix.authentication.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * you need to provide the OAuth2 server a mechanism to authenticate users and return the user information about the
 * authenticating user. This is done by defining two beans in your Spring WebSecurityConfigurerAdapter implementation:
 *
 * authenticationManagerBean()
 * userDetailsServiceBean()
 *
 * These two beans are exposed by using the default authentication authenticationManagerBean() and userDetailsServiceBean() methods from
 * the parent WebSecurityConfigurerAdapter class.
 * These beans are injected into the configure(AuthorizationServerEndpointsConfigurer endpoints) method in JWTOAuth2Config class and
 * used to configure the /auth/oauth/token and /auth/user endpoints
 *
 * The Spring user credentials and security roles can be stored in an:
 *
 * in-memory database
 * relational database
 * LDAP (Active Directory) server
 *
 * ---------------------------------------------------------------------------------------------------------------------
 * IMPORTANT: All the overridden methods and their associated beans in this class will be bootstraped and registered during
 *            service startup.
 *            I have not seen them referenced during runtime. My guess is that Spring will authenticate any call to a
 *            protected resource by implicitly retrieving this information from memory.
 * ---------------------------------------------------------------------------------------------------------------------
 */
@Configuration
public class WebSecurityConfigurer extends WebSecurityConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfigurer.class);

    //The AuthenticationManagerBean is used by Spring Security to handle authentication
    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        logger.debug("### authentication.WebSecurityConfigurer.authenticationManagerBean() - HIT");
        return super.authenticationManagerBean();
    }

    //The UserDetailsService is used by Spring Security to handle user information that will be returned the Spring Security
    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        logger.debug("### authentication.WebSecurityConfigurer.userDetailsServiceBean() - HIT");
        return super.userDetailsServiceBean();
    }

    //The configure() method is where you’ll define users(accounts), their passwords, and their roles
    //Spring can store and retrieve user information (the individual user’s credentials and the roles assigned to the user)
    //from an in-memory data store, a JDBC-backed relational database, or an LDAP server
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        logger.debug("### authentication.WebSecurityConfigurer.configure() - Users and Roles defined");
        auth
                .inMemoryAuthentication()
                .withUser("john.carnell").password("password1").roles("USER")
                .and()
                .withUser("william.woodward").password("password2").roles("USER", "ADMIN");
    }
}
