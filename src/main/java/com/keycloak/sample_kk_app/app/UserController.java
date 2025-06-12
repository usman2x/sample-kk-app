package com.keycloak.sample_kk_app.app;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

	@GetMapping("/api/profile")
	public String getProfile(@AuthenticationPrincipal Jwt jwt) {
		return "Hello, " + jwt.getClaimAsString("preferred_username");
	}
}
