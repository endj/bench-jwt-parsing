package bench;

import org.junit.Test;

import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class JwtParserTest {

    private static final String VALID_PAYLOAD = "{\n"
                                                + "  \"sub\": \"1234567890\",\n"
                                                + "  \"name\": \"John Doe\",\n"
                                                + "  \"iat\": 1516239022\n"
                                                + "}";
    private static final String MISSING_FIELD_VALID_PAYLOAD = "{\n"
                                                              + "  \"sub\": \"1234567890\",\n"
                                                              + "  \"xd\": \"John Doe\",\n"
                                                              + "  \"iat\": 1516239022\n"
                                                              + "}";


    @Test
    public void shouldParse() {
        String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
                     ".eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ" +
                     ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String expectedName = "John Doe";
        String name = JwtParser.parseNameFromJWTErrorHandling(jwt);
        assertThat(name).isEqualTo(expectedName);
    }

    @Test
    public void shouldThrowOnNonJwtString() {
        String notJwt = "hello World";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(notJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowOnDotBait() {
        String notJwt = "adgadg.agdadg";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(notJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowOnDots() {
        String notJwt = "...";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(notJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowOnEmptyRange() {
        String notJwt = "addgda..adgad";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(notJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldThrowOnMissingField() {
        byte[] encode = Base64.getEncoder().encode(MISSING_FIELD_VALID_PAYLOAD.getBytes());
        String jwt = "adgadg." + new String(encode) + ".adgadg";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(jwt))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void shouldParseName() {
        byte[] encode = Base64.getEncoder().encode(VALID_PAYLOAD.getBytes());
        String jwt = "adgadg." + new String(encode) + ".adgadg";
        String expectedName = "John Doe";
        String name = JwtParser.parseNameFromJWTErrorHandling(jwt);
        assertThat(name).isEqualTo(expectedName);
    }

    @Test
    public void shouldThrowOnNonJson() {
        String notJwt = "adgdag.adgadg.agdgda";
        assertThatThrownBy(() -> JwtParser.parseNameFromJWTErrorHandling(notJwt))
                .isInstanceOf(IllegalArgumentException.class);
    }
}