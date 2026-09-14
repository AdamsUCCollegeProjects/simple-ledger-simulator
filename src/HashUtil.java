import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * SHA-256 hashing helper for block content.
 * Identical input always produces the same hexadecimal hash.
 */
public final class HashUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    private HashUtil() {
    }

    public static String sha256(String input) {
        Objects.requireNonNull(input, "input must not be null");
        try {
            MessageDigest digest = MessageDigest.getInstance(HASH_ALGORITHM);
            byte[] hashBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return toHex(hashBytes);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(HASH_ALGORITHM + " is not available", exception);
        }
    }

    private static String toHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int index = 0; index < bytes.length; index++) {
            int value = bytes[index] & 0xFF;
            hexChars[index * 2] = HEX_DIGITS[value >>> 4];
            hexChars[index * 2 + 1] = HEX_DIGITS[value & 0x0F];
        }
        return new String(hexChars);
    }
}
