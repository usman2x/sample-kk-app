package com.keycloak.sample_kk_app.app;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class AuthController {

  @GetMapping("/login")
  public String loginPage() {
    return "login";
  }

  @GetMapping("/register")
  public String registerPage() {
    return "register";
  }

  @PostMapping("/register")
  public String registerUser(@RequestParam String email,
                             @RequestParam String username,
                             @RequestParam String password,
                             Model model) {
    boolean success = keycloakCreateUser(username, email, password);

    if (success) {
      model.addAttribute("msg", "Registration successful. You can now log in.");
      return "redirect:/login";
    } else {
      model.addAttribute("error", "Failed to register. Try again.");
      return "register";
    }
  }

  @GetMapping("/home")
  public String homePage() {
    return "home";
  }

  @PostMapping("/process-login")
  public String processLogin(@RequestParam String username,
                             @RequestParam String password,
                             Model model) {
    String token = keycloakLogin(username, password);
    if (token != null) {
      // Normally, you’d create a session or security context here
      // For demo, we just redirect
      return "redirect:/home";
    } else {
      model.addAttribute("error", "Login failed.");
      return "login";
    }
  }
  private boolean keycloakCreateUser(String username, String email, String password) {
    String keycloakAdminToken = getAdminAccessToken();
    if (keycloakAdminToken == null) return false;

    String keycloakUrl = "http://localhost:8080/admin/realms/{realm}/users"; // Adjust if needed

    RestTemplate restTemplate = new RestTemplate();
    Map<String, Object> user = Map.of(
        "username", username,
        "email", email,
        "enabled", true,
        "credentials", new Object[] {
            Map.of(
                "type", "password",
                "value", password,
                "temporary", false
            )
        }
    );

    var headers = new org.springframework.http.HttpHeaders();
    headers.setBearerAuth(keycloakAdminToken);
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_JSON);

    var request = new org.springframework.http.HttpEntity<>(user, headers);

    try {
      restTemplate.postForEntity(keycloakUrl, request, String.class, "your-realm-name");
      return true;
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  private String keycloakLogin(String username, String password) {
    String tokenUrl = "http://localhost:8080/realms/your-realm-name/protocol/openid-connect/token";

    RestTemplate restTemplate = new RestTemplate();

    var headers = new org.springframework.http.HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

    var body = new org.springframework.util.LinkedMultiValueMap<String, String>();
    body.add("grant_type", "password");
    body.add("client_id", "your-client-id");
    body.add("client_secret", "your-client-secret"); // if confidential client
    body.add("username", username);
    body.add("password", password);

    var request = new org.springframework.http.HttpEntity<>(body, headers);

    try {
      var response = restTemplate.postForEntity(tokenUrl, request, Map.class);
      return (String) response.getBody().get("access_token");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
  private String getAdminAccessToken() {
    String tokenUrl = "http://localhost:8080/realms/master/protocol/openid-connect/token";

    RestTemplate restTemplate = new RestTemplate();

    var headers = new org.springframework.http.HttpHeaders();
    headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

    var body = new org.springframework.util.LinkedMultiValueMap<String, String>();
    body.add("grant_type", "password");
    body.add("client_id", "admin-cli");
    body.add("username", "admin");
    body.add("password", "admin-password");

    var request = new org.springframework.http.HttpEntity<>(body, headers);

    try {
      var response = restTemplate.postForEntity(tokenUrl, request, Map.class);
      return (String) response.getBody().get("access_token");
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

}
