package com.inventory.mgt.sytem.inventory.mgt.system.utils;

import com.inventory.mgt.sytem.inventory.mgt.system.security.UserPrincipal;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import java.util.Date;
@Component
public class TokenProvider {

    private  static Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${app.jwtSecret}")
    private  String jwtSecret;
    @Value("${app.jwtExpirationInMs}")
    private Integer jwtExpirationTimeInMs;


    public  String generateJwtToken(Authentication authentication){
        Date  now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationTimeInMs);
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        return Jwts.builder().setSubject(Long.toString(userPrincipal.getId())).
                setIssuedAt(now).setExpiration(expiryDate).setIssuer("Mr John")
                .signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    public  Long getIdFromJwt(String jwt){
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt).getBody();
        return  Long.parseLong(claims.getSubject());
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        return false;
    }

}
