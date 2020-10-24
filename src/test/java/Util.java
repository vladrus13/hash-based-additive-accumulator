import java.util.Random;

/**
 * Util class for testing
 */
public class Util {
    /**
     * Random
     */
    private static final Random random = new Random(System.currentTimeMillis());
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

    public static byte[] generateRandomByte(long size) {
        byte[] returned = new byte[(int) size];
        random.nextBytes(returned);
        return returned;
    }

    /**
     * Generate int in range [a, b]
     * @param a first
     * @param b second
     * @return random in [a, b]
     */
    public static int generateInRange(int a, int b) {
        return random.nextInt(b - a  + 1) + a;
    }
}
