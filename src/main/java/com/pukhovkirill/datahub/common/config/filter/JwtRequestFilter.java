package com.pukhovkirill.datahub.common.config.filter;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pukhovkirill.datahub.util.JwtUtils;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String offset = "Bearer";
        final String authHeader = request.getHeader("Authorization");

        if(authHeader != null && authHeader.startsWith(offset)){
            String token = authHeader.substring(offset.length()+1);

            try{
                jwtUtils.validateToken(token);

                String username = jwtUtils.getUsername(token);

                if(!username.isBlank() && SecurityContextHolder.getContext().getAuthentication() == null){
                    var upaToken = new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            jwtUtils.getRoles(token).stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())
                    );
                    SecurityContextHolder.getContext().setAuthentication(upaToken);
                }
            }catch(ExpiredJwtException e){
                log.info("Jwt \"time to live\" has expired");
                var message = Map.of(
                        "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Jwt \"time to live\" has expired",
                        "message", e.getMessage());
                response.getWriter().write(convertObjectToJson(message));
            }catch(SignatureException e){
                log.info("Invalid jwt signature");
                var message = Map.of(
                        "timestamp", (new Timestamp(System.currentTimeMillis())).toString(),
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Invalid jwt signature",
                        "message", e.getMessage());
                response.getWriter().write(convertObjectToJson(message));
            }
        }

        filterChain.doFilter(request, response);
    }

    public String convertObjectToJson(Object obj) {
        if (obj == null) return null;
        Gson mapper = new GsonBuilder().setPrettyPrinting().create();
        return mapper.toJson(mapper.toJsonTree(obj));
    }
}
