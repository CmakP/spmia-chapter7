package com.thoughtmechanix.licenses.clients;

import com.thoughtmechanix.licenses.model.Organization;
import com.thoughtmechanix.licenses.utils.UserContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class OrganizationRestTemplateClient {
    @Autowired
    RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(OrganizationRestTemplateClient.class);

    /**
     * Because of the injection of the RestTemplate instance in this class, any outgoing HTTP-based service requests
     * being execute from this RestTemplate instance will go through the registered licenses.UserContextInterceptor
     * and before the REST-based call is made
     *
     * NOTE: This is done to ensure that you can establish a linkage between service calls
     * -----------------------------------------------------------------------------------------------------------------
     * IMPORTANT:
     *            On this note, interceptors are functional only when a service needs to call another service, hence, if
     *            a service never calls another service or resource there's no point in registering an interceptor to it
     * -----------------------------------------------------------------------------------------------------------------
     */
    public Organization getOrganization(String organizationId){
        logger.debug("### licenses.OrganizationRestTemplateClient.getOrganization() - CorrelationId: {}", UserContext.getCorrelationId());

        ResponseEntity<Organization> restExchange =
                restTemplate.exchange(
                        // Next line throws "java.lang.IllegalStateException: No instances available for zuulserver" if @LoadBalanced is placed in Application.getCustomRestTemplate()
//                        "http://zuulserver:5555/api/organization/v1/organizations/{organizationId}",
                        "http://zuulservice/api/organization/v1/organizations/{organizationId}",  // refrencing by Eureka serviceID
                        HttpMethod.GET,
                        null, Organization.class, organizationId);

        return restExchange.getBody();
    }
}
