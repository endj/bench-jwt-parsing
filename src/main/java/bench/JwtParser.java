package bench;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

public class JwtParser {
    private static final String JWT_DELIMITER = ".";
    private static final Base64.Decoder DECODER = Base64.getDecoder();
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final JsonFactory FACTORY = MAPPER.getFactory();
    private static final ObjectReader READER = MAPPER.readerFor(JsonNode.class);
    private static final String NAME_FIELD = "name";
    private static final IllegalArgumentException ILLEGAL_ARGUMENT_EXCEPTION = new IllegalArgumentException("Invalid JWT format");
    private static final IllegalArgumentException NO_NAME_FIELD_EXCEPTION = new IllegalArgumentException("Failed to find name");


    public static String naive(String JWT) {
        try {
            String[] split = JWT.split("\\.");
            Base64.Decoder decoder = Base64.getDecoder();
            String decoded = new String(decoder.decode(split[1]));
            Payload payload = MAPPER.readValue(decoded, Payload.class);
            return payload.getName();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String parseNameFromJWTErrorHandling(String JWT) {
        int first = JWT.indexOf(JWT_DELIMITER) + 1;
        if (first == 0) {
            throw ILLEGAL_ARGUMENT_EXCEPTION;
        }
        int last = JWT.indexOf(JWT_DELIMITER, first);
        if (last == -1 || last == first) {
            throw ILLEGAL_ARGUMENT_EXCEPTION;
        }
        try {
            JsonNode nameField = READER
                    .readTree(DECODER.decode(Arrays.copyOfRange(JWT.getBytes(StandardCharsets.UTF_8), first, last)))
                    .get(NAME_FIELD);
            if (nameField == null)
                throw NO_NAME_FIELD_EXCEPTION;
            return nameField.asText();
        } catch (IOException jpe) {
            throw ILLEGAL_ARGUMENT_EXCEPTION;
        }
    }


    private static final byte[] BUFFER = new byte[4096];

    public static String nonThreadSafeBufferReuse(String JWT) {
        int first = JWT.indexOf(JWT_DELIMITER) + 1;
        if (first == 0) {
            throw new IllegalArgumentException("Invalid JWT format");
        }
        int last = JWT.indexOf(JWT_DELIMITER, first);
        if (last == -1 || last == first) {
            throw new IllegalArgumentException("Invalid JWT format");
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(
                BUFFER,
                0,
                DECODER.decode(
                        Arrays.copyOfRange(JWT.getBytes(StandardCharsets.UTF_8), first, last),
                        BUFFER))) {
            try (JsonParser parser = FACTORY.createParser(bais)) {
                JsonToken token = parser.nextToken();
                while (token != null) {
                    if (token == JsonToken.FIELD_NAME && NAME_FIELD.equals(parser.getCurrentName())) {
                        token = parser.nextToken();
                        if (token == JsonToken.VALUE_STRING) {
                            return parser.getText();
                        } else {
                            break;
                        }
                    }
                    token = parser.nextToken();
                }
            }
            throw new IllegalArgumentException("Failed to find name");
        } catch (IOException jpe) {
            throw new IllegalArgumentException("Failed to parse JWT token");
        }
    }

    public static void main(String[] args) {
        var jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        System.out.println(nonThreadSafeBufferReuse(jwt));
    }
}
