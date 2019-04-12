package com.thoughtmechanix.authentication.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.Arrays;

/**
 * This class will define what applications are registered with your OAuth2 authentication service.
 * The JWTOAuth2Config class defines what applications and the user credentials the OAuth2 service knows about.
 */
@Configuration
public class JWTOAuth2Config extends AuthorizationServerConfigurerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(JWTOAuth2Config.class);

    // defined in WebSecurityConfigurer class
    @Autowired
    private AuthenticationManager authenticationManager;

    // defined in WebSecurityConfigurer class
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    private DefaultTokenServices tokenServices;

    @Autowired
    private JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    private TokenEnhancer jwtTokenEnhancer; // Any custom fields could be injected here before JWT is created

    // This method defines the different components used within the AuthenticationServerConfigurer. This code is telling
    // Spring to use the default authentication manager and user details service that comes up with Spring
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        logger.debug("### authentication.JWTOAuth2Config.configure(AuthorizationServerEndpointsConfigurer endpoints) - HIT");
        //Spring OAuth allows you to hook in multiple token enhancers, so add your token enhancer to a TokenEnhancerChain class
        TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(jwtTokenEnhancer, jwtAccessTokenConverter)); // Any custom fields injected here in the JWT token as it's being created

        endpoints.tokenStore(tokenStore)                             //The token store defined in JWTTokenStoreConfig
                .accessTokenConverter(jwtAccessTokenConverter)       //This is the hook to tell Spring Security OAuth2 code to use JWT
                .tokenEnhancer(tokenEnhancerChain)                   //Hook your token enhancer chain to the endpoints parameter passed into the configure() call
                .authenticationManager(authenticationManager)        //used to configure the /auth/oauth/token
                .userDetailsService(userDetailsService);             //and /auth/user endpoints
    }

    // This defines which clients are going to registered your service
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        logger.debug("### authentication.JWTOAuth2Config.configure(ClientDetailsServiceConfigurer clients) - Registered clients with Service");
        clients.inMemory()
                .withClient("eagleeye")
                .secret("thisissecret")
                .authorizedGrantTypes("refresh_token", "password", "client_credentials")
                .scopes("webclient", "mobileclient");
    }
}