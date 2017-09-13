/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.co.tokaidenshi.tsp.webapi;

import java.io.IOException;
import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.Provider;

/**
 *
 * @author h.ono
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationProvider implements ContainerRequestFilter{
    
    private static final String AUTHENTICATION_SCHEME = "Bearer";
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        if (authenticate(requestContext)) {
            //認証成功
            return;
        }

        //認証失敗
        Result result = new Result();
        result.setCode(-1);
        result.setMessage("invalid.");
        Response response = Response.status(Status.UNAUTHORIZED)
                .entity(result)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .build();
        response.getHeaders().add(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME + " relm=");
        requestContext.abortWith(response);
    }
    
    private boolean isTokenBasedAuthentication(String authorizationHeader) {

        // Check if the Authorization header is valid
        // It must not be null and must be prefixed with "Bearer" plus a whitespace
        // Authentication scheme comparison must be case-insensitive
        return authorizationHeader != null && authorizationHeader.toLowerCase()
                    .startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
    }

    private boolean authenticate(ContainerRequestContext requestContext) {
        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        
        String token = authorizationHeader
                            .substring(AUTHENTICATION_SCHEME.length()).trim();
        
        return "abc".equals(token);
    }   
}
