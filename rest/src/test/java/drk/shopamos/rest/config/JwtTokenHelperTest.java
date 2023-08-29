package drk.shopamos.rest.config;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.User;

import io.jsonwebtoken.ExpiredJwtException;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

class JwtTokenHelperTest {
    private final Clock mockClock = mock(Clock.class);
    private final Clock fixedClock =
            Clock.fixed(Instant.parse("2023-01-01T16:02:42.00Z"), ZoneId.of("Asia/Calcutta"));
    private User user;
    private JwtTokenHelper testee;

    @BeforeEach
    void setup() {
        user = new User();
        user.setEmail("aUsername@adomain.com");
        testee =
                new JwtTokenHelper(
                        "5ffcbb58dc3c78d4296752a9bec8a73bc1e632a12b2b4410540c1e6d5102694",
                        200,
                        mockClock);
        when(mockClock.millis()).thenReturn(fixedClock.millis());
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken is called then returned string is the token base64 encoded with the expected fields")
    void generateToken_tokenIsEncodedCorrectly() {
        String token = testee.generateToken(user);
        assertThat(
                token,
                is(
                        "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhVXNlcm5hbWVAYWRvbWFpbi5jb20iLCJpYXQiOjE2NzI1ODg5NjIsImV4cCI6MTY3MjU4OTE2Mn0.-cLfCW0L_XONOecW85eRMNCTNj2wJN35xYJr9EYXxBk"));
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken with extraClaims is called then returned string is the token base64 encoded with the expected fields plus the extra claims")
    void generateToken_tokenIsEncodedCorrectlyWithExtraClaims() {
        Map<String, Object> extraClaims = Map.of("aClaimName", "aClaimValue");
        String token = testee.generateToken(extraClaims, user);
        assertThat(
                token,
                is(
                        "eyJhbGciOiJIUzI1NiJ9.eyJhQ2xhaW1OYW1lIjoiYUNsYWltVmFsdWUiLCJzdWIiOiJhVXNlcm5hbWVAYWRvbWFpbi5jb20iLCJpYXQiOjE2NzI1ODg5NjIsImV4cCI6MTY3MjU4OTE2Mn0.arha2y7KfOVwnh1qT2JELhk24Zx1r0Z0oWlnP-k9dXk"));
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken with null userDetails is called then IllegalArgumentException is thrown")
    void generateToken_withNullUserDetails() {
        assertThrows(IllegalArgumentException.class, () -> testee.generateToken(null));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token has correct username and not expired then token is valid")
    void isTokenValid_whenCorrectUsername_andNotExpired_thenTokenIsValid() {
        String token = testee.generateToken(user);
        boolean isValid = testee.isTokenValid(token, user);
        assertThat(isValid, is(true));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token is not expired but has incorrect username then token is not valid")
    void isTokenValid_whenIncorrectUsername_thenTokenIsinvalid() {
        String token = testee.generateToken(user);
        user.setEmail("otherUsername@adomain.com");
        boolean isValid = testee.isTokenValid(token, user);
        assertThat(isValid, is(false));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token has correct username but it expired then token is invalid")
    void isTokenValid_whenExpired_thenTokenIsinvalid() {
        String token = testee.generateToken(user);
        when(mockClock.millis()).thenReturn(fixedClock.millis() + 200001);
        assertThrows(ExpiredJwtException.class, () -> testee.isTokenValid(token, user));
    }

    @Test
    @DisplayName("extractUsername - When extractUsername is called then token subject is returned")
    void extractUsername_returnsTokenSubject() throws JSONException {
        String token = testee.generateToken(user);
        String username = testee.extractUsername(token);
        String tokenSubject = getTokenPayload(token).getString("sub");
        assertThat(username, is(tokenSubject));
    }

    @Test
    @DisplayName(
            "extractExpiration - When extractExpiration is called then token expiration is returned")
    void extractExpiration_returnsTokenExpiration() throws JSONException {
        String token = testee.generateToken(user);
        Date expiration = testee.extractExpiration(token);
        Date tokenExpiration = new Date(getTokenPayload(token).getLong("exp") * 1000);
        assertThat(expiration, is(tokenExpiration));
    }

    private JSONObject getTokenPayload(String token) throws JSONException {
        String[] parts = token.split("\\.");
        return new JSONObject(decodeToken(parts[1]));
    }

    private String decodeToken(String token) {
        return new String(Base64.getUrlDecoder().decode(token));
    }
}
