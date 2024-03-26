package com.tiketeer.Tiketeer.auth.jwt;

import static org.assertj.core.api.Assertions.*;

import java.util.Date;

import javax.crypto.SecretKey;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import com.tiketeer.Tiketeer.domain.role.constant.RoleEnum;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;

@SpringBootTest
@DisplayName("JWT Service Test")
class JwtServiceTest {

	private final SecretKey secretKey;
	private final String email = "tiketeer@gmail.com";
	private final RoleEnum roleEnum = RoleEnum.BUYER;
	@Autowired
	private JwtService jwtService;
	@Value("${spring.application.name}")
	private String issuer;
	@Value("${jwt.access-key-expiration-ms}")
	private long accessKeyExpirationInMs;

	public JwtServiceTest(@Value("${jwt.secret-key}") String secretKey) {
		this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
	}

	//Jwts.issuedAt rounds of at ms
	private Date roundOffMillis(Date date) {
		return new Date(date.getTime() / 1000 * 1000);
	}

	@Test
	@DisplayName("페이로드 > JWT 생성 > JWT 디코드 이후 페이로드와 비교")
	void createTokenSuccess() {
		//given
		Date issueDate = new Date(System.currentTimeMillis());
		AccessTokenPayload jwtPayload = new AccessTokenPayload(email, roleEnum, issueDate);

		//when
		String accessToken = jwtService.createAccessToken(jwtPayload);
		assertThat(accessToken).isNotNull();

		Claims payload = Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(accessToken)
			.getPayload();

		//then
		assertThat(payload.getIssuer()).isEqualTo(issuer);
		assertThat(payload.getIssuedAt()).isEqualTo(roundOffMillis(issueDate));
		assertThat(payload.getSubject()).isEqualTo(email);
		assertThat(payload.get("role", String.class)).isEqualTo(roleEnum.name());

	}

	@Test
	@DisplayName("페이로드 > JWT 생성 및 디코드 > sub 비교")
	void verifyTokenSuccess() {
		//given
		Date issueDate = new Date(System.currentTimeMillis());
		AccessTokenPayload jwtPayload = new AccessTokenPayload(email, roleEnum, issueDate);

		//when
		String accessToken = jwtService.createAccessToken(jwtPayload);
		Claims claims = jwtService.verifyToken(accessToken);
		AccessTokenPayload decodedJwtPayload = jwtService.createAccessTokenPayload(claims);

		//then
		Assertions.assertThat(decodedJwtPayload.email()).isEqualTo(email);
	}

	@Test
	@DisplayName("페이로드 > JWT 생성 > 디코드 중 secretKey 불일치")
	void verifyTokenFailBadSecretKey() {
		//given
		Date issueDate = new Date(System.currentTimeMillis());
		AccessTokenPayload jwtPayload = new AccessTokenPayload(email, roleEnum, issueDate);

		//when
		String accessToken = Jwts.builder()
			.subject(jwtPayload.email())
			.claim("role", jwtPayload.roleEnum())
			.issuer(issuer)
			.issuedAt(jwtPayload.issuedAt())
			.expiration(new Date(issueDate.getTime() + accessKeyExpirationInMs))
			.signWith(Jwts.SIG.HS256.key().build())
			.compact();

		//then
		assertThatThrownBy(() -> jwtService.verifyToken(accessToken)).isInstanceOf(SignatureException.class);
	}

	@Test
	@DisplayName("페이로드 > JWT 생성 > 디코드 중 유효기간 오류")
	void verifyTokenFailExpired() {
		//given
		Date issueDate = new Date(System.currentTimeMillis());
		AccessTokenPayload jwtPayload = new AccessTokenPayload(email, roleEnum, issueDate);

		//when
		String accessToken = Jwts.builder()
			.subject(jwtPayload.email())
			.claim("role", jwtPayload.roleEnum())
			.issuer(issuer)
			.issuedAt(jwtPayload.issuedAt())
			.expiration(new Date(issueDate.getTime() - 10000))
			.signWith(secretKey)
			.compact();

		assertThatThrownBy(() -> jwtService.verifyToken(accessToken)).isInstanceOf(ExpiredJwtException.class);

	}
}