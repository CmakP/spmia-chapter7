package com.thoughtmechanix.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @EnableAuthorizationServerannotation annotation tells Spring Cloud that this service will be used as an OAuth2 service
 * and to add several REST-based endpoints that will be used in the OAuth2 authentication and authorization processes
 */
@SpringBootApplication
@RestController
@EnableResourceServer
@EnableAuthorizationServer   //Used to tell Spring Cloud that this service is going to act as an OAuth2 service
public class Application {

    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    //curl  eagleeye:thisissecret@192.168.99.100:8901/auth/oauth/token -d grant_type=password -client_id=eagleeye  -d scope=webclient -d username=william.woodward -d password=password2

    //curl -H "Authorization: Bearer 24fb9e40-21d4-4e78-b211-92edfa2fc514" http://192.168.99.100:8901/auth/user

    /**
      * maps to /auth/user
      - used to retrieve information about the user associated with the token
      - This endpoint is called by the protected service to validate the OAuth2 access token and retrieve the assigned
        roles of the user accessing the protected service.
      - If the OAuth2 access token is valid, the /auth/user endpoint will return information about the user,
        including what roles are assigned to them.
     */
    @RequestMapping(value = { "/user" }, produces = "application/json")
    public Map<String, Object> user(OAuth2Authentication user) {

        Map<String, Object> userInfo = new HashMap<>();
        Authentication userAuthentication = user.getUserAuthentication();
        userInfo.put("user", userAuthentication.getPrincipal());
        userInfo.put("authenticated", userAuthentication.isAuthenticated());
        userInfo.put("authorities", AuthorityUtils.authorityListToSet(userAuthentication.getAuthorities()));
        userInfo.put("details", userAuthentication.getDetails());
        userInfo.put("credentials", userAuthentication.getCredentials());

        logger.debug("### OAth2 Service:8901/auth/user - getUserAuthentication: {}, getPrincipal: {}, getAuthorities: {}", userAuthentication, userInfo.get("user"), userInfo.get("authorities"));
        return userInfo;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
