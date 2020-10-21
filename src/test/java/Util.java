import java.util.Base64;
import java.util.BitSet;

public class Util {
    public BitSet toBitSet(String s) {
        return BitSet.valueOf(Base64.getDecoder().decode(s));
    }

    public String toString(BitSet b) {
        return new String(Base64.getEncoder().encode(b.toByteArray()));
    }
}
