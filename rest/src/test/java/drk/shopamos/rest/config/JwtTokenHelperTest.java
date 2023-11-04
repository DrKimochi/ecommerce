package drk.shopamos.rest.config;

import static drk.shopamos.rest.mother.AccountMother.VIVI_EMAIL;
import static drk.shopamos.rest.mother.AccountMother.buildCustomerNamiWithId;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import drk.shopamos.rest.model.entity.Account;

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
    private static final String SECRET_KEY =
            "5ffcbb58dc3c78d4296752a9bec8a73bc1e632a12b2b4410540c1e6d5102694";
    private static final String TOKEN =
            "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJuYW1pQG11Z2l3YXJhLmNvbSIsImlhdCI6MTY3MjU4ODk2MiwiZXhwIjoxNjcyNTg5MTYyfQ.mFD_2y7w1s0JPLBvTMTmorImEGjeu--euT3ADd9PTTA";
    private static final String TOKEN_WITH_CLAIM =
            "eyJhbGciOiJIUzI1NiJ9.eyJhQ2xhaW1OYW1lIjoiYUNsYWltVmFsdWUiLCJzdWIiOiJuYW1pQG11Z2l3YXJhLmNvbSIsImlhdCI6MTY3MjU4ODk2MiwiZXhwIjoxNjcyNTg5MTYyfQ.6t-v4ePExQwF-1InsNQYOprqgY0IfGRoPufLKE-bPN8";
    private final Clock mockClock = mock(Clock.class);
    private final Clock fixedClock =
            Clock.fixed(Instant.parse("2023-01-01T16:02:42.00Z"), ZoneId.of("Asia/Calcutta"));
    private Account account;
    private JwtTokenHelper testee;

    @BeforeEach
    void setup() {
        account = buildCustomerNamiWithId();
        testee = new JwtTokenHelper(SECRET_KEY, 200, mockClock);
        when(mockClock.millis()).thenReturn(fixedClock.millis());
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken is called then returned string is the token base64 encoded with the expected fields")
    void generateToken_tokenIsEncodedCorrectly() {
        String token = testee.generateToken(account);
        assertThat(token, is(TOKEN));
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken with extraClaims is called then returned string is the token base64 encoded with the expected fields plus the extra claims")
    void generateToken_tokenIsEncodedCorrectlyWithExtraClaims() {
        Map<String, Object> extraClaims = Map.of("aClaimName", "aClaimValue");
        String token = testee.generateToken(extraClaims, account);
        assertThat(token, is(TOKEN_WITH_CLAIM));
    }

    @Test
    @DisplayName(
            "generateToken - When generateToken with null userDetails is called then IllegalArgumentException is thrown")
    void generateToken_withNullUserDetails_thenThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> testee.generateToken(null));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token has correct username and not expired then token is valid")
    void isTokenValid_whenCorrectUsername_andNotExpired_thenTokenIsValid() {
        String token = testee.generateToken(account);
        boolean isValid = testee.isTokenValid(token, account);
        assertThat(isValid, is(true));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token is not expired but has incorrect username then token is not valid")
    void isTokenValid_whenIncorrectUsername_thenTokenIsinvalid() {
        String token = testee.generateToken(account);
        account.setEmail(VIVI_EMAIL);
        boolean isValid = testee.isTokenValid(token, account);
        assertThat(isValid, is(false));
    }

    @Test
    @DisplayName(
            "isTokenValid - When token has correct username but it expired then token is invalid")
    void isTokenValid_whenExpired_thenTokenIsinvalid() {
        String token = testee.generateToken(account);
        when(mockClock.millis()).thenReturn(fixedClock.millis() + 200001);
        assertThrows(ExpiredJwtException.class, () -> testee.isTokenValid(token, account));
    }

    @Test
    @DisplayName("extractUsername - When extractUsername is called then token subject is returned")
    void extractUsername_returnsTokenSubject() throws JSONException {
        String token = testee.generateToken(account);
        String username = testee.extractUsername(token);
        String tokenSubject = getTokenPayload(token).getString("sub");
        assertThat(username, is(tokenSubject));
    }

    @Test
    @DisplayName(
            "extractExpiration - When extractExpiration is called then token expiration is returned")
    void extractExpiration_returnsTokenExpiration() throws JSONException {
        String token = testee.generateToken(account);
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
