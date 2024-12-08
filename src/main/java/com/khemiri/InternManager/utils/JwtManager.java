package com.khemiri.InternManager.utils;

import com.khemiri.InternManager.dto.responses.TokenPayloadResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
@Component
public class JwtManager {

    @Value("${jwt.token}")
    private String secret ;

    public String generateJwtToken(String subject, String role,String id) {
        Long expiration_time = (long) (1000 * 60 * 60);
        String token = Jwts.builder()
                .setSubject(subject)
                .claim("role",role)
                .claim("id",id)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration_time))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
        return token;
    }

    public TokenPayloadResponse validateJwtToken(String jwtToken) {
        try {
            Jws<Claims> claimsJws = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken);
            Claims claims = claimsJws.getBody();
            if(!claims.getExpiration().before(new Date())){
                return  new TokenPayloadResponse(claims.getSubject(),claims.get("role").toString(),claims.get("id").toString());
            }
            return null;

        } catch (Exception e) {
            return null;
        }
    }
}
