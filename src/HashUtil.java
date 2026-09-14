import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Objects;

/**
 * SHA-256 hashing helper for block content.
 * Identical input always produces the same hexadecimal hash.
 */
public final class HashUtil {

    private static final String HASH_ALGORITHM = "SHA-256";

    private HashUtil() {
    }

    public static String sha256(String input) {
        Objects.requireNonNull(input, "input must not be null");
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(HASH_ALGORITHM + " is not available", exception);
        }
    }
}
