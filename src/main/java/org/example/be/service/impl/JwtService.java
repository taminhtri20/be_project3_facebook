package org.example.be.service.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.example.be.modal.JwtToken;
import org.example.be.modal.User;
import org.example.be.modal.UserPrinciple;
import org.example.be.respository.TokenRespository;
import org.example.be.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    @Autowired
    UserService userService;

    @Autowired
    TokenRespository tokenRespository;
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRE_TIME = 86400000000L;
    private static final Logger logger = LoggerFactory.getLogger(JwtService.class.getName());

    public String generateTokenLogin(Authentication authentication) {
        UserPrinciple userPrincipal = (UserPrinciple) authentication.getPrincipal();
        User user = userService.findById(((UserPrinciple) authentication.getPrincipal()).getId()).get();
        JwtToken jwtToken = tokenRespository.findByUser(user);
        if (jwtToken != null) {
            tokenRespository.save(new JwtToken(jwtToken.getId(), user, Jwts.builder()
                    .setSubject((userPrincipal.getUsername()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + EXPIRE_TIME * 1000))
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                    .compact(), true));
            return tokenRespository.findByUser(user).getToken();
        }else {
            tokenRespository.save(new JwtToken(user, Jwts.builder()
                    .setSubject((userPrincipal.getUsername()))
                    .setIssuedAt(new Date())
                    .setExpiration(new Date((new Date()).getTime() + EXPIRE_TIME * 1000))
                    .signWith(SECRET_KEY, SignatureAlgorithm.HS512)
                    .compact(), true));
            return tokenRespository.findByUser(user).getToken();
        }
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature -> Message: {} ", e);
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token -> Message: {}", e);
        } catch (ExpiredJwtException e) {
            logger.error("Expired JWT token -> Message: {}", e);
        } catch (UnsupportedJwtException e) {
            logger.error("Unsupported JWT token -> Message: {}", e);
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty -> Message: {}", e);
        }

        return false;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody().getSubject();
    }
}