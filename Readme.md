
## JWT-Based Security in Spring Boot with Keycloak

This proof-of-concept (PoC) Spring Boot application demonstrates securing backend endpoints using **JWT access tokens** issued by **Keycloak**

### Features Implemented

* **JWT Token Security**

    * The `/api/profile` endpoint is protected and requires a valid JWT access token to access.

* **Token Expiry & Malformation Handling**

    * Expired or tampered tokens are automatically rejected.
    * A **custom `authenticationEntryPoint`** returns a clear message for unauthorized requests (e.g., "Token is expired or invalid").

* **Audience (`aud`) Claim Validation**

    * The backend only accepts tokens that include a specific `aud` (audience) claim.
    * This ensures **tokens issued for one client (frontend, service) can’t be reused across services**.
    * [RFC 9068](https://datatracker.ietf.org/doc/rfc9068/)

---

### Inspect Tokens

Use [https://jwt.io](https://jwt.io) to:

* Paste your token and inspect the **decoded claims**
* Verify `aud`, `exp`, `iat`, `azp`, and `scope` fields

Example audience claim in JWT:

```json
"aud": "java-jwt-hello-world"
```

---

## How to Enable Audience (`aud`) Claim in Keycloak

By default, Keycloak does not include the `aud` claim for your custom clients. To include it:

### Step-by-Step Setup in Keycloak Admin Console

1. **Create a Client Scope for Audience Mapping**

    * Go to **Client Scopes** > **Create Client Scope**

        * Name: `custom-audience`
        * Type: `default`

2. **Add Audience Mapper to Client Scope**

    * Go to **Client Scopes** > Select `custom-audience` > **Mappers** > **Add Mapper (By Configuration)**
        * Select `Audience` as Mapper Type
        * Name: `aud-mapper`
        * Included Client Audience: `java-jwt-hello-world`
        * Add to ID token: ✅
        * Add to access token: ✅
        * Save

3. **Attach the Client Scope to Your Client**

    * Go to **Clients** > Select `java-jwt-hello-world` > **Client Scopes**

        * Add `audience-java-jwt-hello-world` as **default** or **optional scope**

4. **Generate a Token and Validate**

    * Use the following curl command to get a token:

      ```bash
      curl -X POST http://localhost:8080/realms/spring-jwt/protocol/openid-connect/token \
        -H "Content-Type: application/x-www-form-urlencoded" \
        -d "client_id=java-jwt-hello-world" \
        -d "client_secret=fPv7HPZIztJOO8clry53YUMAP4KDErak" \
        -d "username=admin" \
        -d "password=admin" \
        -d "grant_type=password" \
        -d "scope=openid"
      ```
    * Decode the token using [https://jwt.io](https://jwt.io) and check the `aud` field.