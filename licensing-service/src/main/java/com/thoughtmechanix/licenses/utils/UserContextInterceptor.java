package com.thoughtmechanix.licenses.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;

/**
 * The ClientHttpRequestInterceptor is a Spring class that allows you to hook in functionality to be executed
 * before a REST-based call is made.
 *
 * This class is used to inject the correlation ID into any outgoing HTTP-based service requests being executed from a
 * RestTemplate instance. This is done to ensure that you can establish a linkage between service calls.
 */
public class UserContextInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(UserContextInterceptor.class);

    // The intercept() method is invoked before the actual HTTP service call occurs by the RestTemplate.
    @Override
    public ClientHttpResponse intercept(
            HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {

        HttpHeaders headers = request.getHeaders();
        headers.add(UserContext.CORRELATION_ID, UserContextHolder.getContext().getCorrelationId());
        headers.add(UserContext.AUTH_TOKEN, UserContextHolder.getContext().getAuthToken()); //Adding the authorization token to the HTTP header

        logger.debug("### licenses.UserContextInterceptor.intercept() - Added Authorization header from UserContextHolder to the HttpRequest: {}", UserContextHolder.getContext().getAuthToken());

        return execution.execute(request, body);
    }
}