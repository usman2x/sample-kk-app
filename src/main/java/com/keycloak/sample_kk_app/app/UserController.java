package com.keycloak.sample_kk_app.app;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@PreAuthorize("hasAuthority('ROLE_admin')")
	@GetMapping("/api/profile")
	public String getProfile(@AuthenticationPrincipal Jwt jwt) {
		return "Hello, " + jwt.getClaimAsString("preferred_username");
	}

	@GetMapping("/api/roles")
	public String getRoles(Authentication authentication) {
		return "Authorities: " + authentication.getAuthorities();
	}
}
