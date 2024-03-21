package com.tiketeer.Tiketeer.auth.jwt;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

	private final SecretKey secretKey;

	@Value("${spring.application.name}")
	private String issuer;

	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

	@Value("${jwt.refresh-key-expiration-ms}")
	private long refreshKeyExpirationInMs;

	public JwtService(@Value("${jwt.secret-key}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}

	public JwtPayload verifyToken(String jwt) {
		Claims payload = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(jwt)
			.getPayload();

		String roleString = payload.get("role", String.class);
		RoleEnum roleEnum = RoleEnum.valueOf(roleString);

		return new JwtPayload(
			payload.getSubject(),
			roleEnum,
			payload.getIssuedAt()
		);
	}

	public String createToken(JwtPayload jwtPayload) {
		return Jwts.builder()
			.subject(jwtPayload.email())
			.claim("role", jwtPayload.roleEnum().name())
			.issuer(issuer)
			.issuedAt(jwtPayload.issuedAt())
			.expiration(new Date(jwtPayload.issuedAt().getTime() + accessKeyExpirationInMs))
			.signWith(secretKey)
			.compact();
	}

	public String createRefreshToken(JwtPayload jwtPayload) {
		return Jwts.builder()
			.subject(jwtPayload.email())
			.claim("role", jwtPayload.roleEnum().name())
			.issuer(issuer)
			.issuedAt(jwtPayload.issuedAt())
			.expiration(new Date(jwtPayload.issuedAt().getTime() + refreshKeyExpirationInMs))
			.signWith(secretKey)
			.compact();
	}
}
