package com.thoughtmechanix.authentication.security;


import com.thoughtmechanix.authentication.model.UserOrganization;
import com.thoughtmechanix.authentication.repository.OrgUserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class JWTTokenEnhancer implements TokenEnhancer { // You need to extend the TokenEnhancer class

    private static final Logger logger = LoggerFactory.getLogger(JWTTokenEnhancer.class);

    @Autowired
    private OrgUserRepository orgUserRepo;

    // looks up the user’s org ID based on their user name
    private String getOrgId(String userName){
        UserOrganization orgUser = orgUserRepo.findByUserName( userName );
        return orgUser.getOrganizationId();
    }

    // To do this enhancement, you need to add override the enhance() method
    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        Map<String, Object> additionalInfo = new HashMap<>();
        String orgId =  getOrgId(authentication.getName());

        additionalInfo.put("organizationId", orgId);

        logger.debug("### authentication.JWTTokenEnhancer.enhance() - Added organizationId to accessToken: {}", orgId);

        // All additional attributes are placed in a HashMap and set on the accessToken variable passed into the method
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}
