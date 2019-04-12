package com.thoughtmechanix.zuulsvr.filters;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.thoughtmechanix.zuulsvr.config.ServiceConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.xml.bind.DatatypeConverter;

@Component
public class TrackingFilter extends ZuulFilter{
    private static final int      FILTER_ORDER =  1;
    private static final boolean  SHOULD_FILTER=true;
    private static final Logger logger = LoggerFactory.getLogger(TrackingFilter.class);

    @Autowired
    private FilterUtils filterUtils;

    @Autowired
    private ServiceConfig serviceConfig;

    @Override
    public String filterType() {
        return FilterUtils.PRE_FILTER_TYPE;
    }

    @Override
    public int filterOrder() {
        return FILTER_ORDER;
    }

    public boolean shouldFilter() {
        return SHOULD_FILTER;
    }

    private boolean isCorrelationIdPresent(){
      if (filterUtils.getCorrelationId() !=null){
          return true;
      }

      return false;
    }

    private String generateCorrelationId(){
        return java.util.UUID.randomUUID().toString();
    }

    private String getOrganizationId(){

        String result="";
        if (filterUtils.getAuthToken() != null) {

            logger.debug("### zuulsvr.TrackingFilter.TrackingFilter.getOrganizationId() - AuthToken: {}", filterUtils.getAuthToken());
            // Parse out the token out of the Authorization HTTP header
            String authToken = filterUtils.getAuthToken().replace("Bearer ","");
            try {
                Claims claims = Jwts.parser()   //Use JWTS class to parse out the token,
                        .setSigningKey(serviceConfig.getJwtSigningKey().getBytes("UTF-8"))  //passing in the signing key used to sign the token
                        .parseClaimsJws(authToken).getBody();
                result = (String) claims.get("organizationId");  // Pull the organizationId out of the JavaScript token
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        return result;
    }

    public Object run() {

        RequestContext ctx = RequestContext.getCurrentContext();

        if (isCorrelationIdPresent()) {
            logger.debug("### zuul.TrackingFilter.run() - tmx-correlation-id found in tracking filter: {}", filterUtils.getCorrelationId());
        }
        else{
            filterUtils.setCorrelationId(generateCorrelationId());
            logger.debug("### zuul.TrackingFilter.run() - tmx-correlation-id generated in tracking filter: {}", filterUtils.getCorrelationId());
        }

//        System.out.println("zuulsvr.TrackingFilter.run() - The organization id from the token is : " + getOrganizationId());
        logger.debug("### zuulsvr.TrackingFilter.run() - The organization id from the token is : {}",  getOrganizationId());
        filterUtils.setOrgId(getOrganizationId());
        logger.debug("### zuulsvr.TrackingFilter.run() - Processing incoming request for {}",  ctx.getRequest().getRequestURI());
        return null;
    }
}