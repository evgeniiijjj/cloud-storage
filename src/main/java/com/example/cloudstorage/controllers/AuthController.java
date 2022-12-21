package com.example.cloudstorage.controllers;

import com.example.cloudstorage.config.jwt.JwtUtils;
import com.example.cloudstorage.dto.JwtResponse;
import com.example.cloudstorage.dto.LoginRequest;
import com.example.cloudstorage.util.LoggerMessages;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        LOGGER.info(LoggerMessages.START.getMessage());
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        LOGGER.info(LoggerMessages.TRY_AUTHENTICATION.getMessage(loginRequest.getLogin()));
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getLogin(), loginRequest.getPassword()));
        String jwt = jwtUtils.generateJwtToken(authentication);
        LOGGER.info(LoggerMessages.SUCCESS_AUTHENTICATION.getMessage(loginRequest.getLogin()
                , jwtUtils.getExpirationFromJwtToken(jwt)));
        return ResponseEntity.ok(new JwtResponse(jwt));
    }
}
