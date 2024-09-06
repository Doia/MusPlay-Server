package com.andres.curso.springboot.app.springbootcrud.security;

import javax.crypto.SecretKey;

import io.jsonwebtoken.security.Keys;

public class TokenJwtConfig {

    // Reemplaza este valor con tu clave secreta Base64
    private static final String BASE64_SECRET_KEY = "c3VwZXJzZWFyY2hfc2VjcmV0X2tleV9zdHJpbmd2YWx1ZGFyd2F2bHk=";

    // Decodifica la clave Base64 y crea una instancia de SecretKey
    public static final SecretKey SECRET_KEY = Keys.hmacShaKeyFor(
        java.util.Base64.getDecoder().decode(BASE64_SECRET_KEY)
    );
    
    public static final String PREFIX_TOKEN = "Bearer ";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String CONTENT_TYPE = "application/json";

    public static final long ACCESS_TOKEN_EXPIRATION = 3600000; // 1 hora
    public static final long REFRESH_TOKEN_EXPIRATION = 604800000; // 7 días

}
