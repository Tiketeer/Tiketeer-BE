package com.tiketeer.Tiketeer.auth;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityContextHelper {
	public String getEmailInToken() {
		return SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
	}
}
