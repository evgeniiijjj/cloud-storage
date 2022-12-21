package com.example.cloudstorage.config.jwt;

import com.example.cloudstorage.repositories.JwtBlacklistRepository;
import com.example.cloudstorage.service.AuthorizationDataDetails;
import com.example.cloudstorage.util.LoggerMessages;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.example.cloudstorage.CloudStorageApplication.LOGGER;

@Component
public class JwtUtils {

    @Value("${app.jwtsecret}")
    private String jwtSecret;
    @Value("${app.jwtexpirationms}")
    private int jwtExpirationMs;
    @Autowired
    private JwtBlacklistRepository jwtBlacklistRepository;

    public String generateJwtToken(Authentication authentication) {
        AuthorizationDataDetails dataDetails = (AuthorizationDataDetails) authentication.getPrincipal();
        return Jwts.builder().setSubject((dataDetails.getUsername())).setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public boolean validateJwtToken(String jwt) {
        if (jwtBlacklistRepository.existsByJwt(jwt)) {
            LOGGER.info(LoggerMessages.JWT_TOKEN_IN_BLACKLIST.getMessage(jwt));
            return false;
        }
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
            return true;
        } catch (Exception e) {
            LOGGER.info(LoggerMessages.AUTH_BY_TOKEN_ERROR.getMessage(e.getMessage()));
        }
        return false;
    }

    public String getUserNameFromJwtToken(String jwt) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getSubject();
    }

    public String getExpirationFromJwtToken(String jwt) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody().getExpiration().toString();
    }

    public String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("auth-token");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
