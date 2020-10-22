import java.util.Random;

public class Util {
    private static Random random = new Random(System.currentTimeMillis());
    private static String alphabet = "abcdefghijklmnopqrstuvwxyz";

    public static char generateRandomChar() {
        return alphabet.charAt(random.nextInt(alphabet.length()));
    }

    public static String generateRandomString(long size) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(generateRandomChar());
        }
        return sb.toString();
    }
}
