package com.keycloak.sample_kk_app.config;

import org.springframework.security.oauth2.core.*;
import org.springframework.security.oauth2.jwt.*;

public class AudienceValidator implements OAuth2TokenValidator<Jwt> {
  private final String expectedAudience;
  private final OAuth2Error error = new OAuth2Error("invalid_token", "Required audience is missing", null);

  public AudienceValidator(String expectedAudience) {
    this.expectedAudience = expectedAudience;
  }

  @Override
  public OAuth2TokenValidatorResult validate(Jwt jwt) {
    if (jwt.getAudience().contains(expectedAudience)) {
      return OAuth2TokenValidatorResult.success();
    }
    return OAuth2TokenValidatorResult.failure(error);
  }
}

