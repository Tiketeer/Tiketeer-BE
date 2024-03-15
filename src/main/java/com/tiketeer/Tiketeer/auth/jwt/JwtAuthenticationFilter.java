package com.tiketeer.Tiketeer.auth.jwt;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tiketeer.Tiketeer.auth.FilterExceptionResolver;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final FilterExceptionResolver<JwtException> jwtFilterExceptionResolver;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
		FilterChain filterChain) throws ServletException, IOException {

		try {
			final String accessToken = getAccessTokenFromCookie(request);
			JwtPayload jwtPayload = jwtService.verifyToken(accessToken);
			var email = jwtPayload.email();
			var role = jwtPayload.roleEnum().name();
			GrantedAuthority authority = new SimpleGrantedAuthority(role);
			Authentication authentication = new UsernamePasswordAuthenticationToken(email, null, List.of(authority));
			SecurityContextHolder.getContext().setAuthentication(authentication);
			filterChain.doFilter(request, response);

		} catch (JwtException ex) {
			logger.info("Failed to authorize/authenticate with JWT due to " + ex.getMessage());
			jwtFilterExceptionResolver.setResponse(response, ex);
		}

	}

	private String getAccessTokenFromCookie(HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies == null) {
			throw new JwtException("Missing cookie");
		}

		for (Cookie cookie : cookies) {
			if (cookie.getName().equals(JwtMetadata.ACCESS_TOKEN)) {
				return cookie.getValue();
			}
		}
		throw new JwtException("Missing Token");
	}
}
