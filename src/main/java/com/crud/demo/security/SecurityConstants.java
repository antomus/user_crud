package com.crud.demo.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/api/auth/**";
    public static final Key JWT_SECRET = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long EXPIRATION_TIME = 6000000L;
}
