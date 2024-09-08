package com.andres.curso.springboot.app.springbootcrud.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import com.andres.curso.springboot.app.springbootcrud.entities.AuthToken;
import com.andres.curso.springboot.app.springbootcrud.entities.User;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ErrorMessages;
import com.andres.curso.springboot.app.springbootcrud.exceptions.ServerException;
import com.andres.curso.springboot.app.springbootcrud.repositories.MyAuthTokenRepository;
import com.andres.curso.springboot.app.springbootcrud.repositories.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import java.util.List;
import static com.andres.curso.springboot.app.springbootcrud.security.TokenJwtConfig.*;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private MyAuthTokenRepository authRepository;

    @Autowired
    private UserRepository userRepository;

    public Map<String, String> login(String identifier, String password) {
        User user = getUserByIdentifier(identifier);
        
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), password)
        );

        AuthToken authToken = authRepository.findByUsername(user.getUsername())
            .orElse(null);

        String refreshToken = generateRefreshToken(user);

        if (authToken == null) {
            authRepository.save(new AuthToken(user.getUsername(), refreshToken));
        } else {
            authToken.setRefreshToken(refreshToken);
            authRepository.save(authToken);
        }
    
        String accessToken = generateAccessToken(user);
    
        Map<String, String> response = new HashMap<>();
        response.put("token", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("username", user.getUsername());
    
        return response;
    }

    public Map<String, String> refreshToken(String refreshToken) {
        // Verificar la validez del refreshToken
        try {
            Claims claims = Jwts.parser().verifyWith(SECRET_KEY).build().parseSignedClaims(refreshToken).getPayload();

            // Obtener la fecha de expiración del token
            Date expirationDate = claims.getExpiration();

            // Verificar si el token ha expirado
            if (expirationDate.before(new Date())) {
                throw new ServerException(ErrorMessages.INVALID_REFRESH_TOKEN);
            }

            
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // Token ya expirado
            throw new ServerException(ErrorMessages.INVALID_REFRESH_TOKEN);
        } catch (JwtException e) {
            // Token inválido por otras razones
            throw new ServerException(ErrorMessages.INVALID_REFRESH_TOKEN);
        }


        AuthToken authToken = authRepository.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new ServerException(ErrorMessages.INVALID_REFRESH_TOKEN));

        User user = userRepository.findByUsername(authToken.getUsername())
                .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));

        String newAccessToken = generateAccessToken(user);
        String newRefreshToken = generateRefreshToken(user);

        // Actualizar el refreshToken en la base de datos
        authToken.setRefreshToken(newRefreshToken);
        authRepository.save(authToken);

        Map<String, String> response = new HashMap<>();
        response.put("token", newAccessToken);
        response.put("refreshToken", newRefreshToken);

        return response;
    }

    private String generateAccessToken(User user) {
        try {
            // Crear una lista de objetos con la estructura requerida
            List<RoleDTO> roleDTOs = user.getRoles().stream()
                .map(role -> new RoleDTO(role.getName())) // Crear objetos RoleDTO con el formato esperado
                .collect(Collectors.toList());

            // Convertir la lista a JSON
            ObjectMapper objectMapper = new ObjectMapper();
            String rolesJson = objectMapper.writeValueAsString(roleDTOs);
    
            Claims  claims = Jwts.claims()
            .add("authorities", rolesJson)
            .add("id", user.getId())
            .add("username", user.getUsername())
            .build();

            return Jwts.builder()
                .claims(claims)
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error al generar el token de acceso", e);
            }
    }
    
    private String generateRefreshToken(User user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION))
                .signWith(SECRET_KEY)
                .compact();
    }

    private User getUserByIdentifier(String identifier) {
        User user = userRepository.findByUsername(identifier)
                    .orElse(null);
        if (user == null){
            user = userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new ServerException(ErrorMessages.USER_NOT_FOUND));
        }
        return user;
    }

    // DTO para representar el formato de los roles
    private static class RoleDTO {
        private String authority;

        public RoleDTO(String authority) {
            this.authority = authority;
        }

        public String getAuthority() {
            return authority;
        }

        public void setAuthority(String authority) {
            this.authority = authority;
        }
    }
}

