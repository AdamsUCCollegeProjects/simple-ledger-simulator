/**
 * Optional ANSI colors for console presentation output.
 */
public final class AnsiColors {

    public static final String GREEN = "\u001B[32m";
    public static final String RED = "\u001B[31m";
    public static final String YELLOW = "\u001B[33m";

    private static final String RESET = "\u001B[0m";

    private AnsiColors() {
    }

    public static String colorize(String message, String color, boolean isEnabled) {
        if (!isEnabled) {
            return message;
        }
        return color + message + RESET;
    }
}
