package com.pukhovkirill.datahub.util;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    public String getUsername(String token) {
        final JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSecretKey(secret))
                .build();
        return jwtParser.parseSignedClaims(token).getPayload().getSubject();
    }

    public List<String> getRoles(String token) {
        final JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSecretKey(secret))
                .build();

        Claims claims = jwtParser.parseSignedClaims(token).getPayload();

        final List<String> roles = new ArrayList<>();
        ArrayList<?> rawList = claims.get("roles", ArrayList.class);
        if(rawList != null){
            for(Object obj : rawList){
                if (obj instanceof String item) {
                    roles.add(item);
                } else {
                    throw new ClassCastException("Unexpected element type in roles list");
                }
            }
        }

        return roles;
    }

    public void validateToken(String token){
        final JwtParser jwtParser = Jwts.parser()
                .verifyWith(getSecretKey(secret))
                .build();
        jwtParser.parse(token);
    }

    private SecretKey getSecretKey(String secret){
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
