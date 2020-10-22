import java.util.Random;

/**
 * Util class for testing
 */
public class Util {
    /**
     * Random
     */
    private static Random random = new Random(System.currentTimeMillis());
    /**
     * Alphabet for Base64
     */
    private static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    /**
     * Generate random char
     * @return random char
     */
    public static char generateRandomChar() {
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }

    /**
     * Generate random string
     * @param size size of stirng
     * @return random string
     */
    public static String generateRandomString(long size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(generateRandomChar());
        }
        return sb.toString();
    }
}
