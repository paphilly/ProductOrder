package com.konark.util;

import org.springframework.stereotype.Component;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.Claims;


import org.springframework.stereotype.Component;

import java.util.Date;

import com.konark.model.UserModel;

@Component
public class JwtUtil {

	private static final String SECRET = "my-secret-key-change-this";
	private static final long EXPIRATION = 60 * 60 * 1000; // 1 hour

	public String generateToken(String username) {
		return Jwts.builder()
			.setSubject(username)
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
			.signWith(SignatureAlgorithm.HS256, SECRET)
			.compact();
	}

	public String extractUsername(String token) {
		return Jwts.parser()
			.setSigningKey(SECRET)
			.parseClaimsJws(token)
			.getBody()
			.getSubject();
	}

	public String generateToken( UserModel user) {
		return Jwts.builder()
			.setSubject(user.getUsername())
			.claim("user", user.getUsername())
			.claim("role", user.getRole())
			.claim( "email",user.getEmail())
		//	.claim("dept", user.getDepartment())
			.setIssuedAt(new Date())
			.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
			.signWith(SignatureAlgorithm.HS256, SECRET)
			.compact();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
			.setSigningKey(SECRET)
			.parseClaimsJws(token)
			.getBody();
	}

}
