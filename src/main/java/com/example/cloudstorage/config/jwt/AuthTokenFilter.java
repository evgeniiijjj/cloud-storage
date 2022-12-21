package com.example.cloudstorage.config.jwt;

import com.example.cloudstorage.service.AuthorizationDataService;
import com.example.cloudstorage.util.LoggerMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private AuthorizationDataService service;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        String jwt = jwtUtils.parseJwt(httpServletRequest);
        LOGGER.info(LoggerMessages.TRY_AUTHENTICATION_BY_TOKEN.getMessage());
        if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
            String login = jwtUtils.getUserNameFromJwtToken(jwt);
            LOGGER.info(LoggerMessages.AUTH_BY_TOKEN_SUCCESS.getMessage(login));
            UserDetails userDetails = service.loadUserByUsername(login);
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails
                    , null, userDetails.getAuthorities());
            authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
