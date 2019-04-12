package com.thoughtmechanix.organization.security;


import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;

/**
 * Defining access control rules: who and what can access the service
 * The first thing you’re going to do is protect the organization service so that it can only be accessed by an
 * authenticated user.
 */
@Configuration
public class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

    // All access rules are configured off the HttpSecurity object passed into the method
    @Override
    public void configure(HttpSecurity http) throws Exception{
        //All the access rules are defined inside the overridden configure() method
        http
          .authorizeRequests()
          .antMatchers(HttpMethod.DELETE, "/v1/organizations/**") // The antMatchers() method allows you to restrict the URL and HTTP post that’s protected
          .hasRole("ADMIN") //The hasRole() method is a comma-separated list of roles that can be accessed
          .anyRequest()     //The last part of the authorization rule definition still defines that any other endpoint
          .authenticated(); //in your service needs to be access by an authenticated user
    }
}
