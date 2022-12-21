package com.example.cloudstorage.config;

import com.example.cloudstorage.config.jwt.AuthTokenFilter;
import com.example.cloudstorage.config.jwt.JwtUtils;
import com.example.cloudstorage.dto.ErrorResponse;
import com.example.cloudstorage.entities.JwtBlacklist;
import com.example.cloudstorage.repositories.JwtBlacklistRepository;
import com.example.cloudstorage.service.AuthorizationDataService;
import com.example.cloudstorage.util.LoggerMessages;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthorizationDataService service;
    private final JwtBlacklistRepository jwtBlacklistRepository;
    private final JwtUtils jwtUtils;

    public WebSecurityConfig(AuthorizationDataService service, JwtBlacklistRepository jwtBlacklistRepository, JwtUtils jwtUtils) {
        this.service = service;
        this.jwtBlacklistRepository = jwtBlacklistRepository;
        this.jwtUtils = jwtUtils;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder.userDetailsService(service).passwordEncoder(new BCryptPasswordEncoder());
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure (HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated().and()
                .logout().logoutSuccessHandler((request, response, authentication) -> {
                    String jwt = jwtUtils.parseJwt(request);
                    if (jwt != null) jwtBlacklistRepository.save(new JwtBlacklist(jwt, jwtUtils.getExpirationFromJwtToken(jwt)));
                    response.setStatus(HttpServletResponse.SC_OK);
                    LOGGER.info(LoggerMessages.LOGOUT.getMessage(jwtUtils.getUserNameFromJwtToken(jwt), jwt));
                });
        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            if (authException.getClass().equals(InsufficientAuthenticationException.class)) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
            response.getWriter().print(new ErrorResponse(authException.getMessage(), 0));
            LOGGER.info(LoggerMessages.AUTH_BY_TOKEN_FAILED.getMessage());
        });
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
